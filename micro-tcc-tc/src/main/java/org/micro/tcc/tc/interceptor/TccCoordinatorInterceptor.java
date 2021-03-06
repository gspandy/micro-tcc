package org.micro.tcc.tc.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.micro.tcc.tc.annotation.TccTransaction;
import org.micro.tcc.common.core.TransactionXid;
import org.micro.tcc.common.core.Invocation;
import org.micro.tcc.common.core.TransactionMember;
import org.micro.tcc.common.core.Transaction;
import org.micro.tcc.tc.util.ReflectionUtils;
import org.micro.tcc.tc.component.TransactionManager;


import java.lang.reflect.Method;

/**
 *@author jeff.liu
 *   参与者拦截后具体实现
 * date 2019/7/31
 */
@Slf4j
public class TccCoordinatorInterceptor {

    private TransactionManager transactionManager= TransactionManager.getInstance();


    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public Object interceptTransactionContextMethod(ProceedingJoinPoint pjp) throws Throwable {

        Transaction transaction = transactionManager.getCurrentTransaction();

        if (transaction != null) {

            switch (transaction.getStatus()) {
                case TRY:
                    addingParticipants(pjp);
                    break;
                case CONFIRM:
                    break;
                case CANCEL:
                    break;
                default:
                    break;
            }
        }


        return pjp.proceed(pjp.getArgs());
    }

    private void addingParticipants(ProceedingJoinPoint pjp) throws IllegalAccessException, InstantiationException {

        Method method = ReflectionUtils.getTccTransactionMethod(pjp);
        if (method == null) {
            throw new RuntimeException(String.format("join point not found method, point is : %s", pjp.getSignature().getName()));
        }
        TccTransaction tccTransaction = method.getAnnotation(TccTransaction.class);

        String confirmMethodName = tccTransaction.confirmMethod();
        String cancelMethodName = tccTransaction.cancelMethod();

        Transaction transaction = transactionManager.getCurrentTransaction();
        TransactionXid xid = new TransactionXid(transaction.getTransactionXid().getGlobalTccTransactionId());
        
        Class targetClass = ReflectionUtils.getDeclaringType(pjp.getTarget().getClass(), method.getName(), method.getParameterTypes());
        Invocation confirmInvocation = new Invocation(targetClass, confirmMethodName, method.getParameterTypes(), pjp.getArgs());
        Invocation cancelInvocation = new Invocation(targetClass, cancelMethodName, method.getParameterTypes(), pjp.getArgs());
        TransactionMember participant = new TransactionMember(xid, confirmInvocation, cancelInvocation);
        transactionManager.addingParticipants(participant);

    }


}
