package com.indigo.core.exception.enums;

import lombok.Getter;

/**
 * 标准错误码和消息
 *
 * @author 史偕成
 * @date 2024/03/21
 **/
@Getter
public enum ErrorCode {
    // 系统级错误码
    BASE_SUCCESS("200", "操作成功"),
    BASE_ERROR("SYS000", "系统基础错误"),
    ILLEGAL_STATE("SYS011", "非法状态"),
    SYSTEM_ERROR("SYS001", "系统内部错误"),
    PARAM_ERROR("SYS002", "参数错误"),
    UNAUTHORIZED("SYS003", "未授权"),
    FORBIDDEN("SYS004", "禁止访问"),
    NOT_FOUND("SYS005", "资源不存在"),
    METHOD_NOT_ALLOWED("SYS006", "方法不允许"),
    REQUEST_TIMEOUT("SYS007", "请求超时"),
    TOO_MANY_REQUESTS("SYS008", "请求过于频繁"),
    RATE_LIMIT_EXCEEDED("SYS012", "请求频率超出限制"),
    SERVICE_UNAVAILABLE("SYS009", "服务不可用"),
    GATEWAY_ERROR("SYS010", "网关错误"),

    // 业务级错误码
    BUSINESS_ERROR("BUS001", "业务处理错误"),
    DATA_NOT_FOUND("BUS002", "数据不存在"),
    DATA_ALREADY_EXISTS("BUS003", "数据已存在"),
    DATA_INVALID("BUS004", "数据无效"),
    OPERATION_FAILED("BUS005", "操作失败"),
    OPERATION_TIMEOUT("BUS006", "操作超时"),
    OPERATION_FORBIDDEN("BUS007", "操作被禁止"),
    OPERATION_NOT_ALLOWED("BUS008", "操作不允许"),
    OPERATION_INVALID("BUS009", "操作无效"),
    OPERATION_CONFLICT("BUS010", "操作冲突"),

    // 安全认证相关错误码
    SECURITY_ERROR("SEC001", "安全错误"),
    NOT_LOGIN("SEC002", "未登录"),
    LOGIN_FAILED("SEC003", "登录失败"),
    LOGIN_EXPIRED("SEC004", "登录已过期"),
    TOKEN_INVALID("SEC005", "令牌无效"),
    TOKEN_EXPIRED("SEC006", "令牌已过期"),
    TOKEN_MISSING("SEC007", "令牌缺失"),
    PERMISSION_DENIED("SEC008", "权限不足：缺少功能权限"),
    ROLE_DENIED("SEC009", "权限不足：缺少角色权限"),
    ACCOUNT_DISABLED("SEC010", "账号被封禁"),
    ACCOUNT_LOCKED("SEC011", "账号已锁定"),
    MFA_REQUIRED("SEC012", "需要二级认证"),
    MFA_FAILED("SEC013", "二级认证失败"),
    SESSION_INVALID("SEC014", "会话无效"),
    SESSION_EXPIRED("SEC015", "会话已过期"),
    OAUTH_ERROR("SEC016", "OAuth认证错误"),
    OAUTH_INVALID_CLIENT("SEC017", "OAuth客户端无效"),
    OAUTH_INVALID_GRANT("SEC018", "OAuth授权无效"),
    OAUTH_ACCESS_DENIED("SEC019", "OAuth访问被拒绝"),

    // IAM相关错误码
    IAM_ERROR("IAM001", "身份认证错误"),
    USER_NOT_FOUND("IAM002", "用户不存在"),
    USER_ALREADY_EXISTS("IAM003", "用户已存在"),
    USER_INVALID("IAM004", "用户无效"),
    USER_DISABLED("IAM005", "用户已禁用"),
    USER_LOCKED("IAM006", "用户已锁定"),
    USER_EXPIRED("IAM007", "用户已过期"),
    USER_CREDENTIALS_INVALID("IAM008", "用户凭证无效"),
    USER_CREDENTIALS_EXPIRED("IAM009", "用户凭证已过期"),
    USER_CREDENTIALS_LOCKED("IAM010", "用户凭证已锁定"),

    // 工作流相关错误码
    WORKFLOW_ERROR("WF001", "工作流错误"),
    WORKFLOW_NOT_FOUND("WF002", "工作流不存在"),
    WORKFLOW_ALREADY_EXISTS("WF003", "工作流已存在"),
    WORKFLOW_INVALID("WF004", "工作流无效"),
    WORKFLOW_DISABLED("WF005", "工作流已禁用"),
    WORKFLOW_LOCKED("WF006", "工作流已锁定"),
    WORKFLOW_EXPIRED("WF007", "工作流已过期"),
    WORKFLOW_INSTANCE_NOT_FOUND("WF008", "工作流实例不存在"),
    WORKFLOW_INSTANCE_INVALID("WF009", "工作流实例无效"),
    WORKFLOW_INSTANCE_COMPLETED("WF010", "工作流实例已完成"),

    // 审计相关错误码
    AUDIT_ERROR("AUD001", "审计错误"),
    AUDIT_NOT_FOUND("AUD002", "审计记录不存在"),
    AUDIT_ALREADY_EXISTS("AUD003", "审计记录已存在"),
    AUDIT_INVALID("AUD004", "审计记录无效"),
    AUDIT_DISABLED("AUD005", "审计已禁用"),
    AUDIT_LOCKED("AUD006", "审计已锁定"),
    AUDIT_EXPIRED("AUD007", "审计已过期"),
    AUDIT_OPERATION_NOT_ALLOWED("AUD008", "审计操作不允许"),
    AUDIT_OPERATION_INVALID("AUD009", "审计操作无效"),
    AUDIT_OPERATION_CONFLICT("AUD010", "审计操作冲突");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}