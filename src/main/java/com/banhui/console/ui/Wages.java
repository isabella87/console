package com.banhui.console.ui;

import java.util.ArrayList;
import java.util.List;

public class Wages {

    private final static int Month= 12;
    private final static double MonthWages= 100000;
    private final static double SOCIAL_SECURITY_BASE=5000;   //社保基数
    private final static double SOCIAL_SECURITY_COST=5000;        //1837.5;   //五险一金
    private final static double CHILD_EDUCATION_COST=0;   //子女教育
    private final static double PARENT_SUPPORT_COST=1000;   //父母赡养
    private final static double CONTINUING_EDUCATION_COST=400;  //继续教育
    private final static double HOUSING_LOAN_COST=1000;  //住房贷款

    private static double[] gradles={36000,144000,300000,420000,660000,960000}; //级数分界值
    private static float[] preReduceRate={3,10,20,25,30,35,45};  // 预扣率
    private static double[] preReduce={0,2520,16920,31920,52920,85920,181920};  //速算扣除数

    public static void main(String[] args){
        double totalTaxs = 0.00;
        double totalWages = 0.00;
        List<Double> taxs =new ArrayList<>();
        double tax =0.00;
        for(int i=1;i<=Month;i++){
            double wagesWaitingForReduceTax = MonthWages* i - (SOCIAL_SECURITY_BASE + SOCIAL_SECURITY_COST+CHILD_EDUCATION_COST+PARENT_SUPPORT_COST+CONTINUING_EDUCATION_COST+HOUSING_LOAN_COST) * i;

            for(int j = 0;j<gradles.length;j++){
                if(wagesWaitingForReduceTax<= gradles[j]){
                    tax= wagesWaitingForReduceTax * preReduceRate[j] /100  -preReduce[j];
                    for(int m=0;m<i-1;m++){
                        tax -=taxs.get(m);
                    }
                    taxs.add(tax);
                    System.out.println("第"+i+"月，税费为： " + tax + "  元");
                    System.out.println("第"+i+"月，实际工资为： "+(MonthWages -SOCIAL_SECURITY_COST - tax) +"  元");

                    totalTaxs+=tax;
                    totalWages+=MonthWages -SOCIAL_SECURITY_COST - tax;

                    break;
                }
            }


        }
        System.out.println("累计税收为："+ totalTaxs );
        System.out.println("累计工资为：" + totalWages );

    }

    public void wages(){

    }
}


