package org.micro.tcc.tc.recover;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TransactionRecoverConfig implements RecoverConfig {

    public static final TransactionRecoverConfig INSTANCE = new TransactionRecoverConfig();

    @Value("${transaction.recover.maxRetryCount:30}")
    private int maxRetryCount = 30;

    //160 秒
    @Value("${transaction.recover.recoverDuration:160}")
    private int recoverDuration = 160;
    @Value("${transaction.recover.cronExpression:}")
    private String cronExpression = "";

    @Value("${transaction.recover.zk.cronExpression:}")
    private String cronZkExpression = "";

    public static TransactionRecoverConfig getInstance(){
        return INSTANCE;
    }



    public TransactionRecoverConfig() {

    }

    public String getCronZkExpression() {
        return cronZkExpression;
    }

    public void setCronZkExpression(String cronZkExpression) {
        this.cronZkExpression = cronZkExpression;
    }

    @Override
    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    @Override
    public int getRecoverDuration() {
        return recoverDuration;
    }

    @Override
    public String getCronExpression() {
        return cronExpression;
    }


    public void setMaxRetryCount(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    public void setRecoverDuration(int recoverDuration) {
        this.recoverDuration = recoverDuration;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }


}
