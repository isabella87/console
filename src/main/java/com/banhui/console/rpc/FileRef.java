package com.banhui.console.rpc;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Objects;

/**
 * 文件引用。
 */
public final class FileRef {
    private final long id;
    private final long objectId;
    private final long fileType;
    private final String name;
    private final long size;
    private final Date lastModifiedTime;
    private final String description;
    private final byte[] brief;
    private final String hash;

    public FileRef(
            long id,
            long objectId,
            long fileType,
            String name,
            long size,
            Date lastModifiedTime,
            String description,
            byte[] brief,
            String hash
    ) {
        this.id = id;
        this.objectId = objectId;
        this.fileType = fileType;
        this.name = name.trim();
        this.size = size;
        this.lastModifiedTime = lastModifiedTime;
        this.description = description.trim();
        this.brief = brief;
        this.hash = StringUtils.trimToEmpty(hash);
    }

    public final long getId() {
        return this.id;
    }

    public final long getObjectId() {
        return this.objectId;
    }

    public final long getFileType() {
        return this.fileType;
    }

    public final String getName() {
        return this.name;
    }

    public final long getSize() {
        return this.size;
    }

    public final Date getLastModifiedTime() {
        return this.lastModifiedTime;
    }

    public final String getDescription() {
        return this.description;
    }

    public final byte[] getBrief() {
        return this.brief;
    }

    public final String getHash() {
        return this.hash;
    }

    public final int hashCode() {
        return Objects.hash(this.id);
    }

    public final boolean equals(Object other) {
        if (!(other instanceof FileRef)) {
            return false;
        } else {
            FileRef o = (FileRef) other;
            return Objects.equals(this.id, o.id);
        }
    }

    public final String toString() {
        return "FileRef(id=" + this.id + ")";
    }
}

