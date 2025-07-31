package com.indigo.events;

import com.indigo.events.core.Event;
import com.indigo.events.core.EventPriority;
import com.indigo.events.core.EventStatus;
import com.indigo.events.transaction.TransactionStatus;
import com.indigo.events.transaction.TransactionState;
import com.indigo.events.utils.EventUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 事件测试类
 * 验证基础事件模型的功能
 *
 * @author 史偕成
 * @date 2025/01/08
 */
@DisplayName("事件模型测试")
public class EventTest {
    
    private String testTransactionId;
    private String testEventType;
    private String testSourceService;
    private Map<String, Object> testEventData;
    
    @BeforeEach
    void setUp() {
        testTransactionId = EventUtils.generateTransactionId();
        testEventType = "user.created";
        testSourceService = "user-service";
        testEventData = new HashMap<>();
        testEventData.put("userId", "123");
        testEventData.put("username", "testuser");
    }
    
    @Test
    @DisplayName("测试事件创建")
    public void testEventCreation() {
        // 创建事件
        Event event = EventUtils.createSimpleEvent(testTransactionId, testEventType, testSourceService, testEventData);
        
        // 验证事件属性
        assertNotNull(event.getEventId(), "事件ID不能为空");
        assertEquals(testTransactionId, event.getTransactionId(), "事务ID应该匹配");
        assertEquals(testEventType, event.getEventType(), "事件类型应该匹配");
        assertEquals(testSourceService, event.getSourceService(), "源服务应该匹配");
        assertEquals(EventStatus.PENDING, event.getStatus(), "初始状态应该是PENDING");
        assertEquals(EventPriority.NORMAL, event.getPriority(), "默认优先级应该是NORMAL");
        assertEquals("1.0", event.getVersion(), "默认版本应该是1.0");
        assertEquals(0, event.getRetryCount(), "初始重试次数应该是0");
        assertEquals(3, event.getMaxRetryCount(), "默认最大重试次数应该是3");
        assertNotNull(event.getCreatedTime(), "创建时间不能为空");
        assertNotNull(event.getUpdatedTime(), "更新时间不能为空");
        assertNull(event.getErrorMessage(), "初始错误信息应该为空");
    }
    
    @Test
    @DisplayName("测试事件状态更新")
    public void testEventStatusUpdate() {
        // 创建事件
        Event event = EventUtils.createSimpleEvent(testTransactionId, testEventType, testSourceService, testEventData);
        
        // 更新状态为处理中
        event.updateStatus(EventStatus.PROCESSING);
        assertEquals(EventStatus.PROCESSING, event.getStatus(), "状态应该更新为PROCESSING");
        
        // 更新状态为成功
        event.updateStatus(EventStatus.SUCCESS);
        assertEquals(EventStatus.SUCCESS, event.getStatus(), "状态应该更新为SUCCESS");
        
        // 验证更新时间已更新
        assertNotNull(event.getUpdatedTime(), "更新时间应该已更新");
    }
    
    @Test
    @DisplayName("测试事件重试机制")
    public void testEventRetry() {
        // 创建事件
        Event event = EventUtils.createSimpleEvent(testTransactionId, testEventType, testSourceService, testEventData);
        
        // 初始重试次数为0
        assertEquals(0, event.getRetryCount(), "初始重试次数应该是0");
        assertTrue(event.canRetry(), "初始状态应该可以重试");
        
        // 增加重试次数
        event.incrementRetryCount();
        assertEquals(1, event.getRetryCount(), "重试次数应该增加到1");
        assertTrue(event.canRetry(), "重试次数1应该可以重试");
        
        // 继续增加重试次数
        event.incrementRetryCount();
        event.incrementRetryCount();
        assertEquals(3, event.getRetryCount(), "重试次数应该增加到3");
        assertFalse(event.canRetry(), "重试次数3不应该可以重试");
    }
    
    @Test
    @DisplayName("测试事件错误处理")
    public void testEventError() {
        // 创建事件
        Event event = EventUtils.createSimpleEvent(testTransactionId, testEventType, testSourceService, testEventData);
        
        // 设置错误信息
        String errorMessage = "处理失败";
        event.setError(errorMessage);
        
        assertEquals(errorMessage, event.getErrorMessage(), "错误信息应该设置正确");
        assertNotNull(event.getUpdatedTime(), "设置错误时应该更新更新时间");
    }
    
    @Test
    @DisplayName("测试高优先级事件")
    public void testUrgentEvent() {
        // 创建高优先级事件
        Event event = EventUtils.createUrgentEvent(testTransactionId, testEventType, testSourceService, testEventData);
        
        assertEquals(EventPriority.URGENT, event.getPriority(), "优先级应该是URGENT");
        assertTrue(event.getPriority().isUrgent(), "URGENT优先级应该是紧急的");
        assertTrue(event.getPriority().isHigherThan(EventPriority.NORMAL), "URGENT应该比NORMAL优先级高");
    }
    
    @Test
    @DisplayName("测试系统事件")
    public void testSystemEvent() {
        // 创建系统事件
        Event event = EventUtils.createSystemEvent(testTransactionId, testEventType, testSourceService, testEventData);
        
        assertEquals(EventPriority.SYSTEM, event.getPriority(), "优先级应该是SYSTEM");
        assertTrue(event.getPriority().isSystem(), "SYSTEM优先级应该是系统级");
        assertTrue(event.getPriority().isUrgent(), "SYSTEM优先级应该是紧急的");
        assertTrue(event.getPriority().isHigherThan(EventPriority.URGENT), "SYSTEM应该比URGENT优先级高");
    }
    
    @Test
    @DisplayName("测试事件有效性检查")
    public void testEventValidation() {
        // 有效事件
        Event validEvent = EventUtils.createSimpleEvent(testTransactionId, testEventType, testSourceService, testEventData);
        assertTrue(EventUtils.isValidEvent(validEvent), "有效事件应该通过验证");
        
        // 无效事件 - 缺少事件ID
        Event invalidEvent1 = new Event();
        invalidEvent1.setTransactionId("test");
        invalidEvent1.setEventType("test");
        invalidEvent1.setSourceService("test");
        assertFalse(EventUtils.isValidEvent(invalidEvent1), "缺少事件ID的事件应该验证失败");
        
        // 无效事件 - 缺少事务ID
        Event invalidEvent2 = new Event();
        invalidEvent2.setEventId("test");
        invalidEvent2.setEventType("test");
        invalidEvent2.setSourceService("test");
        assertFalse(EventUtils.isValidEvent(invalidEvent2), "缺少事务ID的事件应该验证失败");
        
        // 无效事件 - 缺少事件类型
        Event invalidEvent3 = new Event();
        invalidEvent3.setEventId("test");
        invalidEvent3.setTransactionId("test");
        invalidEvent3.setSourceService("test");
        assertFalse(EventUtils.isValidEvent(invalidEvent3), "缺少事件类型的事件应该验证失败");
        
        // 无效事件 - 缺少源服务
        Event invalidEvent4 = new Event();
        invalidEvent4.setEventId("test");
        invalidEvent4.setTransactionId("test");
        invalidEvent4.setEventType("test");
        assertFalse(EventUtils.isValidEvent(invalidEvent4), "缺少源服务的事件应该验证失败");
        
        // 无效事件 - null
        assertFalse(EventUtils.isValidEvent(null), "null事件应该验证失败");
    }
    
    @Test
    @DisplayName("测试事件重试检查")
    public void testEventRetryCheck() {
        // 创建事件
        Event event = EventUtils.createSimpleEvent(testTransactionId, testEventType, testSourceService, testEventData);
        
        // 初始状态不可以重试（PENDING状态）
        assertFalse(EventUtils.canRetry(event), "初始PENDING状态不应该可以重试");
        
        // 设置为处理中状态
        event.updateStatus(EventStatus.PROCESSING);
        assertFalse(EventUtils.canRetry(event), "处理中状态不应该可以重试");
        
        // 设置为失败状态
        event.updateStatus(EventStatus.FAILED);
        assertTrue(EventUtils.canRetry(event), "失败状态应该可以重试");
        
        // 设置为重试中状态
        event.updateStatus(EventStatus.RETRYING);
        assertTrue(EventUtils.canRetry(event), "重试中状态应该可以重试");
        
        // 设置为成功状态
        event.updateStatus(EventStatus.SUCCESS);
        assertFalse(EventUtils.canRetry(event), "成功状态不应该可以重试");
        
        // 设置为已取消状态
        event.updateStatus(EventStatus.CANCELLED);
        assertFalse(EventUtils.canRetry(event), "已取消状态不应该可以重试");
    }
    
    @Test
    @DisplayName("测试事件回滚检查")
    public void testEventRollbackCheck() {
        // 创建事件
        Event event = EventUtils.createSimpleEvent(testTransactionId, testEventType, testSourceService, testEventData);
        
        // 初始状态不可以回滚（PENDING状态）
        assertFalse(EventUtils.canRollback(event), "初始PENDING状态不应该可以回滚");
        
        // 设置为处理中状态
        event.updateStatus(EventStatus.PROCESSING);
        assertTrue(EventUtils.canRollback(event), "处理中状态应该可以回滚");
        
        // 设置为成功状态
        event.updateStatus(EventStatus.SUCCESS);
        assertTrue(EventUtils.canRollback(event), "成功状态应该可以回滚");
        
        // 设置为失败状态
        event.updateStatus(EventStatus.FAILED);
        assertFalse(EventUtils.canRollback(event), "失败状态不应该可以回滚");
        
        // 设置为已取消状态
        event.updateStatus(EventStatus.CANCELLED);
        assertFalse(EventUtils.canRollback(event), "已取消状态不应该可以回滚");
    }
    
    @Test
    @DisplayName("测试事务状态创建")
    public void testTransactionStatusCreation() {
        // 创建事务状态
        String businessType = "order.create";
        int timeoutSeconds = 300;
        
        TransactionStatus status = EventUtils.createTransactionStatus(testTransactionId, businessType, timeoutSeconds);
        
        // 验证事务状态
        assertEquals(testTransactionId, status.getTransactionId(), "事务ID应该匹配");
        assertEquals(businessType, status.getBusinessType(), "业务类型应该匹配");
        assertEquals(TransactionState.ACTIVE, status.getState(), "初始状态应该是ACTIVE");
        assertEquals(0, status.getTotalEvents(), "初始总事件数应该是0");
        assertEquals(0, status.getSuccessEvents(), "初始成功事件数应该是0");
        assertEquals(0, status.getFailedEvents(), "初始失败事件数应该是0");
        assertEquals(0, status.getPendingEvents(), "初始待处理事件数应该是0");
        assertNotNull(status.getCreatedTime(), "创建时间不能为空");
        assertNotNull(status.getUpdatedTime(), "更新时间不能为空");
        assertNotNull(status.getTimeoutTime(), "超时时间不能为空");
        assertNull(status.getErrorMessage(), "初始错误信息应该为空");
    }
    
    @Test
    @DisplayName("测试事务状态更新")
    public void testTransactionStatusUpdate() {
        // 创建事务状态
        TransactionStatus status = EventUtils.createTransactionStatus(testTransactionId, "test.business", 300);
        
        // 添加事件
        status.addEvent("event1");
        assertEquals(1, status.getTotalEvents(), "总事件数应该是1");
        assertEquals(1, status.getPendingEvents(), "待处理事件数应该是1");
        
        // 事件处理成功
        status.eventSuccess();
        assertEquals(1, status.getSuccessEvents(), "成功事件数应该是1");
        assertEquals(0, status.getPendingEvents(), "待处理事件数应该是0");
        assertEquals(TransactionState.SUCCESS, status.getState(), "状态应该是SUCCESS");
        
        // 验证事务完成
        assertTrue(EventUtils.isTransactionCompleted(status), "事务应该已完成");
        assertTrue(EventUtils.isTransactionSuccess(status), "事务应该成功");
        assertFalse(EventUtils.isTransactionFailed(status), "事务不应该失败");
    }
    
    @Test
    @DisplayName("测试事务失败状态")
    public void testTransactionFailure() {
        // 创建事务状态
        TransactionStatus status = EventUtils.createTransactionStatus(testTransactionId, "test.business", 300);
        
        // 添加事件
        status.addEvent("event1");
        status.addEvent("event2");
        
        // 第一个事件成功
        status.eventSuccess();
        assertEquals(TransactionState.ACTIVE, status.getState(), "还有待处理事件时状态应该是ACTIVE");
        
        // 第二个事件失败
        status.eventFailed();
        assertEquals(TransactionState.FAILED, status.getState(), "有失败事件时状态应该是FAILED");
        
        // 验证事务状态
        assertTrue(EventUtils.isTransactionCompleted(status), "事务应该已完成");
        assertFalse(EventUtils.isTransactionSuccess(status), "事务不应该成功");
        assertTrue(EventUtils.isTransactionFailed(status), "事务应该失败");
    }
    
    @Test
    @DisplayName("测试事务超时检查")
    public void testTransactionTimeout() {
        // 创建事务状态，设置很短的超时时间
        TransactionStatus status = EventUtils.createTransactionStatus(testTransactionId, "test.business", 1);
        
        // 等待超时
        try {
            Thread.sleep(1100); // 等待1.1秒，确保超时
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        assertTrue(EventUtils.isTransactionTimeout(status), "事务应该超时");
    }
    
    @Test
    @DisplayName("测试事务错误设置")
    public void testTransactionError() {
        // 创建事务状态
        TransactionStatus status = EventUtils.createTransactionStatus(testTransactionId, "test.business", 300);
        
        // 设置错误信息
        String errorMessage = "事务处理失败";
        status.setError(errorMessage);
        
        assertEquals(errorMessage, status.getErrorMessage(), "错误信息应该设置正确");
        assertNotNull(status.getUpdatedTime(), "设置错误时应该更新更新时间");
    }
    
    @Test
    @DisplayName("测试事件状态枚举")
    public void testEventStatusEnum() {
        // 测试终态检查
        assertTrue(EventStatus.SUCCESS.isFinal(), "SUCCESS应该是终态");
        assertTrue(EventStatus.FAILED.isFinal(), "FAILED应该是终态");
        assertTrue(EventStatus.CANCELLED.isFinal(), "CANCELLED应该是终态");
        assertTrue(EventStatus.ROLLBACK.isFinal(), "ROLLBACK应该是终态");
        assertFalse(EventStatus.PENDING.isFinal(), "PENDING不应该是终态");
        assertFalse(EventStatus.PROCESSING.isFinal(), "PROCESSING不应该是终态");
        
        // 测试重试检查
        assertTrue(EventStatus.FAILED.canRetry(), "FAILED应该可以重试");
        assertTrue(EventStatus.RETRYING.canRetry(), "RETRYING应该可以重试");
        assertFalse(EventStatus.SUCCESS.canRetry(), "SUCCESS不应该可以重试");
        assertFalse(EventStatus.PENDING.canRetry(), "PENDING不应该可以重试");
        
        // 测试回滚检查
        assertTrue(EventStatus.SUCCESS.canRollback(), "SUCCESS应该可以回滚");
        assertTrue(EventStatus.PROCESSING.canRollback(), "PROCESSING应该可以回滚");
        assertFalse(EventStatus.FAILED.canRollback(), "FAILED不应该可以回滚");
        assertFalse(EventStatus.CANCELLED.canRollback(), "CANCELLED不应该可以回滚");
    }
    
    @Test
    @DisplayName("测试事件优先级枚举")
    public void testEventPriorityEnum() {
        // 测试优先级比较
        assertTrue(EventPriority.HIGH.isHigherThan(EventPriority.NORMAL), "HIGH应该比NORMAL优先级高");
        assertTrue(EventPriority.URGENT.isHigherThan(EventPriority.HIGH), "URGENT应该比HIGH优先级高");
        assertTrue(EventPriority.SYSTEM.isHigherThan(EventPriority.URGENT), "SYSTEM应该比URGENT优先级高");
        assertFalse(EventPriority.NORMAL.isHigherThan(EventPriority.HIGH), "NORMAL不应该比HIGH优先级高");
        
        // 测试系统级检查
        assertTrue(EventPriority.SYSTEM.isSystem(), "SYSTEM应该是系统级");
        assertFalse(EventPriority.URGENT.isSystem(), "URGENT不应该是系统级");
        
        // 测试紧急检查
        assertTrue(EventPriority.URGENT.isUrgent(), "URGENT应该是紧急的");
        assertTrue(EventPriority.SYSTEM.isUrgent(), "SYSTEM应该是紧急的");
        assertFalse(EventPriority.NORMAL.isUrgent(), "NORMAL不应该是紧急的");
    }
    
    @Test
    @DisplayName("测试事务状态枚举")
    public void testTransactionStateEnum() {
        // 测试终态检查
        assertTrue(TransactionState.SUCCESS.isFinal(), "SUCCESS应该是终态");
        assertTrue(TransactionState.FAILED.isFinal(), "FAILED应该是终态");
        assertTrue(TransactionState.ROLLBACK.isFinal(), "ROLLBACK应该是终态");
        assertTrue(TransactionState.TIMEOUT.isFinal(), "TIMEOUT应该是终态");
        assertTrue(TransactionState.CANCELLED.isFinal(), "CANCELLED应该是终态");
        assertFalse(TransactionState.ACTIVE.isFinal(), "ACTIVE不应该是终态");
        assertFalse(TransactionState.ROLLBACKING.isFinal(), "ROLLBACKING不应该是终态");
        
        // 测试回滚检查
        assertTrue(TransactionState.ACTIVE.canRollback(), "ACTIVE应该可以回滚");
        assertTrue(TransactionState.SUCCESS.canRollback(), "SUCCESS应该可以回滚");
        assertTrue(TransactionState.FAILED.canRollback(), "FAILED应该可以回滚");
        assertFalse(TransactionState.ROLLBACKING.canRollback(), "ROLLBACKING不应该可以回滚");
        assertFalse(TransactionState.ROLLBACK.canRollback(), "ROLLBACK不应该可以回滚");
        
        // 测试成功检查
        assertTrue(TransactionState.SUCCESS.isSuccess(), "SUCCESS应该是成功状态");
        assertFalse(TransactionState.FAILED.isSuccess(), "FAILED不应该是成功状态");
        
        // 测试失败检查
        assertTrue(TransactionState.FAILED.isFailure(), "FAILED应该是失败状态");
        assertTrue(TransactionState.TIMEOUT.isFailure(), "TIMEOUT应该是失败状态");
        assertTrue(TransactionState.CANCELLED.isFailure(), "CANCELLED应该是失败状态");
        assertFalse(TransactionState.SUCCESS.isFailure(), "SUCCESS不应该是失败状态");
    }
} 