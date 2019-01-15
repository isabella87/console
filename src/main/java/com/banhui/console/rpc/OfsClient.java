package com.banhui.console.rpc;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.xx.armory.commons.SysUtils.readString;
import static org.xx.armory.commons.Validators.notBlank;
import static org.xx.armory.commons.Validators.notNull;
import static org.xx.armory.http.SSLContextUtils.ignoreCert;

public final class OfsClient
        implements AutoCloseable {
    /**
     * JSON序列化类。
     */
    private static final ObjectMapper JSON_MAPPER = createObjectMapper();
    private static final MapType STRING_MAP_TYPE = JSON_MAPPER.getTypeFactory().constructMapType(LinkedHashMap.class, String.class, Object.class);
    private static final CollectionType STRING_MAP_LIST_TYPE = JSON_MAPPER.getTypeFactory().constructCollectionType(ArrayList.class, STRING_MAP_TYPE);

    private static final RequestConfig REQUEST_CONFIG = RequestConfig.custom()
                                                                     .setExpectContinueEnabled(true)
                                                                     .setContentCompressionEnabled(true)
                                                                     .setConnectTimeout(5 * 1000)
                                                                     .setConnectionRequestTimeout(2 * 1000)
                                                                     .setSocketTimeout(8 * 1000)
                                                                     .build();
    private static final String CONTENT_DISPOSITION = "Content-Disposition";
    private final String baseUrl;
    private HttpClient httpClient;

    public OfsClient() {
        this.httpClient = HttpClientBuilder.create()
                                           .setUserAgent(getClass().toString())
                                           .setRetryHandler(StandardHttpRequestRetryHandler.INSTANCE)
                                           .setDefaultRequestConfig(REQUEST_CONFIG)
                                           .setSSLContext(ignoreCert())
                                           .setRedirectStrategy(DefaultRedirectStrategy.INSTANCE)
                                           .build();
        String baseUrl = "http://192.168.11.30/p2psrv/"; //test获取文件服务配置地址

//        String baseUrl = Application.settings().getProperty("rpc-base-uri"); //获取文件服务配置地址

        if (baseUrl.contains("p2psrv")) {
            baseUrl = baseUrl.replace("p2psrv", "ofs");
        } else {
            baseUrl += baseUrl + "ofs/";
        }

        if (!baseUrl.startsWith("http://") && !baseUrl.startsWith("https://")) {
            baseUrl = "https://" + baseUrl;
        }

        this.baseUrl = baseUrl;
    }

    /**
     * 创建公用的JSON序列化类。
     *
     * @return 公用的JSON序列化类。
     * @see ObjectMapper#setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include)
     * @see com.fasterxml.jackson.annotation.JsonInclude.Include#NON_EMPTY
     */
    private static ObjectMapper createObjectMapper() {
        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private void checkResponse(
            HttpResponse response
    ) {
        final StatusLine statusLine = response.getStatusLine();
        if (statusLine == null) {
            throw new IllegalStateException("cannot get status");
        }
        final int statusCode = statusLine.getStatusCode();
        final String reasonPhrase = statusLine.getReasonPhrase();
        if (statusCode < 200) {
            throw new IllegalStateException("illegal status code: " + statusCode);
        } else if (statusCode > 299 && statusCode < 400) {
            throw new IllegalStateException("illegal status code: " + statusCode);
        } else if (statusCode > 399 && statusCode < 500) {
            throw new IllegalStateException("cannot read response: " + statusCode + ", " + reasonPhrase);
        } else if (statusCode > 499) {
            throw new IllegalStateException("ofs server error: " + statusCode + ", " + reasonPhrase);
        }
    }

    private String readStringContent(
            HttpResponse response
    )
            throws IOException {
        return readString(response.getEntity().getContent(), UTF_8);
    }

    public List<FileRef> list(
            long objectId,
            long fileType
    )
            throws IOException {
        final HttpGet request = new HttpGet(this.baseUrl + "root?object-id=" + objectId + "&file-type=" + fileType);

        final HttpResponse response = this.httpClient.execute(request);
        checkResponse(response);

        final String content = readStringContent(response);
        final List<Map<String, Object>> values = JSON_MAPPER.readValue(content, STRING_MAP_LIST_TYPE);
        return values.stream()
                     .map(this::asFileRef)
                     .collect(Collectors.toList());
    }

    public Optional<FileRef> upload(
            long objectId,
            long fileType,
            String fileName,
            String description,
            long fileSize,
            InputStream fileContent
    )
            throws IOException {
        final HttpPost request = new HttpPost(this.baseUrl + "/root?object-id=" + objectId + "&file-type=" + fileType);

        final HttpEntity entity = MultipartEntityBuilder.create()
                                                        .setCharset(UTF_8)
                                                        .setContentType(ContentType.MULTIPART_FORM_DATA)
                                                        .setMode(HttpMultipartMode.RFC6532)
                                                        .addTextBody("description", description != null ? description : "", ContentType.TEXT_PLAIN.withCharset(UTF_8))
                                                        .addBinaryBody("file", fileContent, ContentType.APPLICATION_OCTET_STREAM, fileName)
                                                        .build();
        request.setEntity(entity);

        final HttpResponse response = this.httpClient.execute(request);
        checkResponse(response);

        final String content = readStringContent(response);
        if (isBlank(content)) {
            return Optional.empty();
        } else {
            final Map<String, Object> value = JSON_MAPPER.readValue(content, STRING_MAP_TYPE);
            return Optional.ofNullable(asFileRef(value));
        }
    }

    public void download(
            long id,
            String hash,
            String filePath
    )
            throws IOException {
        final HttpGet request = new HttpGet(this.baseUrl + "/" + id + "/" + hash);
        final HttpResponse response = this.httpClient.execute(request);
        checkResponse(response);
        try (InputStream is = new BufferedInputStream(response.getEntity().getContent());
             OutputStream os = new FileOutputStream(filePath)
        ) {
            byte[] by = new byte[1024];
            int rc;
            while ((rc = is.read(by, 0, by.length)) > 0) {
                os.write(by, 0, rc);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean unlink(
            long id,
            String hash
    ) {
        final HttpDelete request = new HttpDelete(this.baseUrl + "/" + id + "/" + hash);
        final HttpResponse response;
        try {
            response = this.httpClient.execute(request);
            checkResponse(response);
            final String content = readStringContent(response);
            switch (content) {
                case "true":
                    return true;
                case "false":
                    return false;
                default:
                    return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void close()
            throws Exception {
        if (this.httpClient instanceof AutoCloseable) {
            ((AutoCloseable) this.httpClient).close();
        }
    }

    private long longValue(
            Map<String, Object> map,
            String key
    ) {
        notNull(map, "map");
        notBlank(key, "key");

        final Object value = map.get(key);
        if (value != null) {
            if (value instanceof Number) {
                return ((Number) value).longValue();
            } else {
                return 0L;
            }
        } else {
            return 0L;
        }
    }

    private String stringValue(
            Map<String, Object> map,
            String key
    ) {
        notNull(map, "map");
        notBlank(key, "key");

        final Object value = map.get(key);

        return value != null ? String.valueOf(value) : "";
    }

    private FileRef asFileRef(
            Map<String, Object> map
    ) {
        if (map != null) {
            final String brief = stringValue(map, "brief");
            return new FileRef(
                    longValue(map, "id"),
                    longValue(map, "objectId"),
                    longValue(map, "fileType"),
                    stringValue(map, "name"),
                    longValue(map, "size"),
                    new Date(longValue(map, "lastModifiedTime")),
                    stringValue(map, "description"),
                    !isBlank(brief) ? Base64.getDecoder().decode(brief) : new byte[0],
                    stringValue(map, "hash")
            );
        } else {
            return null;
        }
    }

    public static void main(String[] args)
            throws IOException {

        OfsClient ofsClient = new OfsClient();
//        File file = new File("C:\\Users\\Administrator\\Desktop\\my.txt");

//        InputStream inputStream = new FileInputStream(file);
//        ofsClient.upload(463, 23, "abc汉字哟", "哈哈上传文件", file.length(), inputStream);
        List<FileRef> frList = ofsClient.list(463, 23);
        for (FileRef fr : frList) {
            System.out.println(fr.getName() + fr.getId() + fr.getFileType() + fr.getObjectId());
        }
    }
}
