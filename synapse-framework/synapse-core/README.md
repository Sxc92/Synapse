# Synapse Core 核心框架

## 📖 概述

Synapse Core 是 SynapseMOM 平台的核心基础框架，提供了通用的工具类、异常处理、配置管理、国际化支持等基础功能。该模块为整个平台提供了统一的基础设施支持。

## ✨ 特性

- 🛠️ **工具类集合**: 提供丰富的工具类，包括断言、集合、日期时间、字符串等
- 🚨 **异常处理**: 统一的异常处理机制和自定义异常类
- 🌍 **国际化支持**: 多语言支持，支持中文和英文
- ⚙️ **配置管理**: 统一的配置管理和自动配置
- 🔄 **序列化**: 自定义序列化配置，支持 JSON 处理
- 🧵 **线程池**: 统一的线程池配置和管理
- 🌐 **Web MVC**: Web MVC 配置和拦截器
- 📊 **实体类**: 通用的实体类和数据结构

## 🏗️ 模块结构

```
synapse-core/
├── config/           # 配置类
│   ├── I18nConfig.java           # 国际化配置
│   ├── ThreadPoolConfig.java     # 线程池配置
│   ├── WebMvcConfig.java         # Web MVC 配置
│   └── serialization/            # 序列化配置
├── constants/        # 常量定义
├── context/         # 上下文管理
│   └── UserContext.java          # 用户上下文
├── entity/          # 实体类
│   ├── Result.java              # 统一返回结果
│   └── TreeNode.java            # 树形结构节点
├── enums/           # 枚举类
├── exception/       # 异常处理
│   ├── BaseException.java       # 基础异常
│   ├── BusinessException.java   # 业务异常
│   ├── AssertException.java     # 断言异常
│   └── handler/                 # 异常处理器
├── interceptor/     # 拦截器
│   └── SecurityContextInterceptor.java
└── utils/           # 工具类
    ├── AssertUtils.java         # 断言工具
    ├── CollectionUtils.java     # 集合工具
    ├── DateTimeUtils.java       # 日期时间工具
    ├── StringUtils.java         # 字符串工具
    └── ...                      # 其他工具类
```

## 🚀 快速开始

### 1. 添加依赖

在 `pom.xml` 中添加依赖：

```xml
<dependency>
    <groupId>com.indigo</groupId>
    <artifactId>synapse-core</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 配置国际化

在 `application.yml` 中配置：

```yaml
spring:
  messages:
    basename: i18n/messages
    encoding: UTF-8
    cache-duration: 3600
```

### 3. 使用工具类

```java
import com.indigo.core.utils.AssertUtils;
import com.indigo.core.utils.DateTimeUtils;
import com.indigo.core.utils.StringUtils;

@Service
public class UserService {
    
    public void createUser(User user) {
        // 使用断言工具
        AssertUtils.notNull(user, "用户信息不能为空");
        AssertUtils.hasText(user.getUsername(), "用户名不能为空");
        
        // 使用字符串工具
        String normalizedUsername = StringUtils.trimToEmpty(user.getUsername());
        
        // 使用日期时间工具
        String currentTime = DateTimeUtils.formatNow("yyyy-MM-dd HH:mm:ss");
        
        // 业务逻辑...
    }
}
```

### 4. 使用统一返回结果

```java
import com.indigo.core.entity.Result;

@RestController
public class UserController {
    
    @GetMapping("/users/{id}")
    public Result<User> getUser(@PathVariable Long id) {
        try {
            User user = userService.findById(id);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error("获取用户信息失败: " + e.getMessage());
        }
    }
}
```

### 5. 使用异常处理

```java
import com.indigo.core.exception.BusinessException;
import com.indigo.core.exception.AssertException;

@Service
public class OrderService {
    
    public void createOrder(Order order) {
        // 使用业务异常
        if (order.getAmount() <= 0) {
            throw new BusinessException("订单金额必须大于0");
        }
        
        // 使用断言异常
        AssertUtils.isTrue(order.getUserId() != null, "用户ID不能为空");
        
        // 业务逻辑...
    }
}
```

## 📋 核心功能

### 工具类

#### 断言工具 (AssertUtils)

```java
// 对象断言
AssertUtils.notNull(user, "用户不能为空");
AssertUtils.isTrue(age > 0, "年龄必须大于0");

// 字符串断言
AssertUtils.hasText(username, "用户名不能为空");
AssertUtils.isEmail(email, "邮箱格式不正确");

// 集合断言
AssertUtils.notEmpty(list, "列表不能为空");
AssertUtils.hasSize(list, 10, "列表大小必须为10");
```

#### 日期时间工具 (DateTimeUtils)

```java
// 格式化日期
String formatted = DateTimeUtils.formatNow("yyyy-MM-dd HH:mm:ss");

// 解析日期
Date date = DateTimeUtils.parse("2023-12-01", "yyyy-MM-dd");

// 日期计算
Date tomorrow = DateTimeUtils.addDays(new Date(), 1);
Date lastWeek = DateTimeUtils.addWeeks(new Date(), -1);

// 获取时间戳
long timestamp = DateTimeUtils.getCurrentTimestamp();
```

#### 集合工具 (CollectionUtils)

```java
// 集合判空
boolean isEmpty = CollectionUtils.isEmpty(list);
boolean isNotEmpty = CollectionUtils.isNotEmpty(list);

// 集合转换
List<String> names = CollectionUtils.extractProperty(users, "name");
Set<Long> ids = CollectionUtils.extractProperty(users, "id");

// 集合分组
Map<String, List<User>> grouped = CollectionUtils.groupBy(users, "department");
```

### 异常处理

#### 异常层次结构

```
BaseException (基础异常)
├── BusinessException (业务异常)
├── AssertException (断言异常)
└── SecurityException (安全异常)
```

#### 自定义异常

```java
// 业务异常
public class UserNotFoundException extends BusinessException {
    public UserNotFoundException(Long userId) {
        super("用户不存在: " + userId);
    }
}

// 断言异常
public class InvalidParameterException extends AssertException {
    public InvalidParameterException(String message) {
        super(message);
    }
}
```

### 国际化支持

#### 消息文件

`i18n/messages.properties`:
```properties
user.not.found=用户不存在
user.create.success=用户创建成功
user.update.success=用户更新成功
user.delete.success=用户删除成功
```

`i18n/messages_zh_CN.properties`:
```properties
user.not.found=用户不存在
user.create.success=用户创建成功
user.update.success=用户更新成功
user.delete.success=用户删除成功
```

`i18n/messages_en.properties`:
```properties
user.not.found=User not found
user.create.success=User created successfully
user.update.success=User updated successfully
user.delete.success=User deleted successfully
```

#### 使用国际化

```java
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

@Service
public class UserService {
    
    @Autowired
    private MessageSource messageSource;
    
    public void createUser(User user) {
        // 获取当前语言环境
        Locale locale = LocaleContextHolder.getLocale();
        
        // 获取国际化消息
        String message = messageSource.getMessage(
            "user.create.success", 
            null, 
            locale
        );
        
        log.info(message);
    }
}
```

### 用户上下文

```java
import com.indigo.core.context.UserContext;

@Service
public class SecurityService {
    
    public void setCurrentUser(User user) {
        UserContext.setCurrentUser(user);
    }
    
    public User getCurrentUser() {
        return UserContext.getCurrentUser();
    }
    
    public void clearCurrentUser() {
        UserContext.clear();
    }
}
```

### 树形结构

```java
import com.indigo.core.entity.TreeNode;

// 创建树形结构
TreeNode<String> root = new TreeNode<>("root", "根节点");
TreeNode<String> child1 = new TreeNode<>("child1", "子节点1");
TreeNode<String> child2 = new TreeNode<>("child2", "子节点2");

root.addChild(child1);
root.addChild(child2);

// 遍历树形结构
root.traverse(node -> {
    System.out.println("节点: " + node.getData());
});
```

## ⚙️ 配置选项

### 线程池配置

```yaml
synapse:
  core:
    thread-pool:
      core-size: 10
      max-size: 20
      queue-capacity: 100
      keep-alive-seconds: 60
      thread-name-prefix: "synapse-core-"
```

### Web MVC 配置

```yaml
synapse:
  core:
    web:
      cors:
        allowed-origins: "*"
        allowed-methods: "GET,POST,PUT,DELETE,OPTIONS"
        allowed-headers: "*"
        allow-credentials: true
```

## 📝 使用示例

### 完整的服务示例

```java
@Service
@Slf4j
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    public Result<Product> createProduct(Product product) {
        try {
            // 参数验证
            AssertUtils.notNull(product, "产品信息不能为空");
            AssertUtils.hasText(product.getName(), "产品名称不能为空");
            AssertUtils.isTrue(product.getPrice() > 0, "产品价格必须大于0");
            
            // 业务逻辑
            Product savedProduct = productRepository.save(product);
            
            log.info("产品创建成功: {}", savedProduct.getId());
            return Result.success(savedProduct);
            
        } catch (BusinessException e) {
            log.error("产品创建失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("产品创建异常", e);
            return Result.error("系统异常，请稍后重试");
        }
    }
    
    public Result<List<Product>> getProductsByCategory(String category) {
        try {
            AssertUtils.hasText(category, "分类不能为空");
            
            List<Product> products = productRepository.findByCategory(category);
            
            if (CollectionUtils.isEmpty(products)) {
                return Result.success(Collections.emptyList());
            }
            
            return Result.success(products);
            
        } catch (Exception e) {
            log.error("获取产品列表异常", e);
            return Result.error("获取产品列表失败");
        }
    }
}
```

### 控制器示例

```java
@RestController
@RequestMapping("/api/products")
@Slf4j
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @PostMapping
    public Result<Product> createProduct(@RequestBody @Valid Product product) {
        return productService.createProduct(product);
    }
    
    @GetMapping("/category/{category}")
    public Result<List<Product>> getProductsByCategory(@PathVariable String category) {
        return productService.getProductsByCategory(category);
    }
    
    @GetMapping("/{id}")
    public Result<Product> getProduct(@PathVariable Long id) {
        try {
            AssertUtils.notNull(id, "产品ID不能为空");
            
            Product product = productService.findById(id);
            if (product == null) {
                return Result.error("产品不存在");
            }
            
            return Result.success(product);
            
        } catch (Exception e) {
            log.error("获取产品详情异常", e);
            return Result.error("获取产品详情失败");
        }
    }
}
```

## 🧪 测试

### 单元测试示例

```java
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    
    @Mock
    private ProductRepository productRepository;
    
    @InjectMocks
    private ProductService productService;
    
    @Test
    void createProduct_Success() {
        // Given
        Product product = new Product();
        product.setName("测试产品");
        product.setPrice(100.0);
        
        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setName("测试产品");
        savedProduct.setPrice(100.0);
        
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
        
        // When
        Result<Product> result = productService.createProduct(product);
        
        // Then
        assertTrue(result.isSuccess());
        assertEquals(savedProduct, result.getData());
        verify(productRepository).save(product);
    }
    
    @Test
    void createProduct_NullProduct_ThrowsException() {
        // When & Then
        assertThrows(AssertException.class, () -> {
            productService.createProduct(null);
        });
    }
}
```

## 📚 相关文档

- [Synapse Events](./synapse-events/README.md) - 事件驱动框架
- [Synapse Cache](./synapse-cache/README.md) - 缓存框架
- [Synapse Security](./synapse-security/README.md) - 安全框架
- [Synapse Databases](./synapse-databases/README.md) - 数据库框架

## 🤝 贡献

欢迎提交 Issue 和 Pull Request 来改进这个框架。

## 📄 许可证

本项目采用 MIT 许可证。

---

**最后更新：** 2025-07-20  
**版本：** 1.0.0  
**维护者：** 史偕成 