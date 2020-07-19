### 背景及简单使用

在项目中，大多数的关键字如权限、角色、下拉菜单的值等都是以数据字典形式（1:管理员,2:普通用户...）存储于数据库中，一般来说，数据库表字段并不会设计用于存储这些代码的翻译值的冗余字段，因此需要在程序中写翻译代码对其进行手动翻译，这就导致每个需要翻译的地方，都需要添加翻译的代码，导致代码冗余。

代码冗余的问题很好解决，可以选择创建utils提取翻译方法，然而这样你仍需要在需要翻译的地方添加至少一行的代码来调用utils的翻译方法。

当你需要对bean中的某个字段做翻译（转换）或特殊处理时，选择datatranslate将给你带来极大的便利，<b>在bean的字段上添加翻译所需的注解即可完成其对应数据的翻译</b>，举个栗子

```xml
<dependency>
	<groupId>cn.cycode.datatranslate</groupId>
	<artifactId>datatranslate</artifactId>
	<version>1.0</version>
</dependency>
```

```java
public class User {

	private String username;

	@SensitiveField(prefix = 0, replaceWith = '*')
	private String password;
	
	private String nickname;

	@SensitiveField(prefix = 3, suffix = 4, replaceWith = '*')
	private String tel;
    // Getter、Setter、Constructor...
}
```

```java
public class TestTranslate {

	public static void main(String[] args) {
		User user = new User("testUsername", "password@123", "walic", "12345678901");
		Object data = TranslateExecutor.translate(user);
		System.out.println(data);
	}
}
```

TranslateExecutor.translate()方法会将user对象转换为json对象，以上代码执行结果为

```json
{"password":"************","nickname":"walic","tel":"123****8901","username":"testUsername"}
```

@SensitiveField为datatranslate自带的字段脱敏注解，从结果中可以看到user对象的password、tel字段已成功脱敏且以指定格式返回，是不是很方便呢？

### 自定义翻译器

上述例子中使用的<b>@SensitiveField</b>为datatranslate自带的翻译注解，其对应有一个翻译器为其提供翻译实现。能不能自己制作翻译器呢？当然可以，你只需要以下几个步骤即可完成自定义翻译器的制作

- 创建注解类

```java
/**
 * 自定义的用于 性别 翻译的注解
 */
@Documented
@Retention(RUNTIME)
@Target({ FIELD })
public @interface SexField {}
```

- 创建翻译器

```java
/**
 * 自定义的用于 性别 翻译的翻译器
 */
public class SexTranslator extends AbstractTranslator {

	@Override
	protected Class<? extends Annotation> bindAnnotationClass() {
		return SexField.class;
	}

	@Override
	public Object translate(Object fieldValue, Field field, Object obj) {
		if (fieldValue == null) {
			return null;
		}
		Integer sex = (Integer) fieldValue;
		return sex == 1 ? "男" : "女";
	}
}
```

- 在bean的字段添加注解

```java
public class User {
	private String username;

	@SensitiveField(prefix = 0, replaceWith = '*')
	private String password;
	private String nickname;

	@SensitiveField(prefix = 3, suffix = 4, replaceWith = '*')
	private String tel;

	@SexField
	private Integer sex;
    // Getter、Setter、Constructor...
}
```

- 注册定义翻译器并使用

```java
public class TestTranslate {

	public static void main(String[] args) {
		TranslatorRegistry.registry(new SexTranslator());

		User user1 = new User("testUsername", "password@123", "walic", "12345678901", 1);
		User user2 = new User("testUsername", "password@123", "walic", "12345678901", 0);

		Object data1 = TranslateExecutor.translate(user1);
		Object data2 = TranslateExecutor.translate(user2);
		System.out.println(data1);
		System.out.println(data2);
	}
}
```

- 查看结果

```json
{"password":"************","sex":"男","nickname":"walic","tel":"123****8901","username":"testUsername"}
{"password":"************","sex":"女","nickname":"walic","tel":"123****8901","username":"testUsername"}
```

### springboot中的使用

datatranslate提供了springboot的starter以便其能在springboot更方便地使用

- 引入starter依赖

```xml
<dependency>
    <groupId>cn.cycode.datatranslate</groupId>
    <artifactId>datatranslate-boot-starter</artifactId>
    <version>1.0</version>
</dependency>
```

- 在启动类上引入<b>DataTranslateConfiguration</b>配置类

```java
@ImportAutoConfiguration(classes = { DataTranslateConfiguration.class })
@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

- 在Controller中使用<b>@DataTranslate</b>注解

```java
@RestController
public class TestController {
	@GetMapping
	@DataTranslate
	public Object testTranslate() {
		return new User("walic", "walic@7890", "walic", "12345678901");
	}
}
```

- 调用接口并查看返回结果

```json
{"password":"**********","nickname":"walic","tel":"123****8901","username":"walic"}
```

#### springboot中自定义翻译器

在springboot中制作一个自定义翻译器则更简单，这里直接使用上面自定义的<b>@SexField</b>和<b>SexTranslator</b>

- 在自定义翻译器类上添加@Component注解即可完成翻译器的注册

```java
@Component
public class SexTranslator extends AbstractTranslator {
	// ...
}
```

- 对Controller做一些简单修改

```java
@RestController
public class TestController {
	@GetMapping
	@DataTranslate
	public Object testTranslate() {
		List<User> users = new ArrayList<>();
		users.add(new User("walic1", "walic@7890", "walic1", "12345678901", 1));
		users.add(new User("walic2", "walic@7890", "walic2", "12345678902", 0));
		return users;
	}
}
```

- 调用接口并查看结果

```json
[{"password":"**********","sex":"男","nickname":"walic1","tel":"123****8901","username":"walic1"},{"password":"**********","sex":"女","nickname":"walic2","tel":"123****8902","username":"walic2"}]
```

你会发现，datatranslate居然还能翻译集合中的对象，是的，<b>它不仅能翻译集合中的对象，还能对数组、Map中的对象进行翻译</b>。

### 一些额外的技巧

#### 嵌套对象的翻译

若bean中有嵌套对象，比如下面的情况

```java
public class User {

	private String username;

	@SensitiveField(prefix = 0, replaceWith = '*')
	private String password;
	private String nickname;

	@SexField
	private Integer sex;

	private User lover;
    // Getter、Setter、Constructor...
}
```

User中又包含了类型为User（当然还可以是其他类型）的lover属性，这种情况下datatranslate并不会为 lover 做翻译工作，为所有的非基本类型字段做翻译工作是件非常耗费性能的事情，当然如果你确实需要lover的翻译数据，你可以在这个字段上添加<b>@ObjectField注解以标识它为需要翻译的嵌套类型字段</b>。

#### 切忌循环依赖

bean的循环依赖会直接导致<b>StackOverflowError</b>

#### 保证bean的字段有Getter方法

#### 抑制遍历翻译

datatranslate是默认会翻译集合、数组、Map对象的，在某些情况下，你可能并不需要这些翻译工作，那么就把它抑制住好了，试试<b>@SuppressArray、@SuppressCollection、@SuppressMap</b>吧。

```java
public class User {

	private String username;

	@SensitiveField(prefix = 0, replaceWith = '*')
	private String password;
	private String nickname;

	@SexField
	private Integer sex;

    @SuppressCollection
	private List<User> lovers;
    
    @SuppressMap
    private Map<String,User> family;
    
    @SuppressArray
    private User[] friends;
    // Getter、Setter、Constructor...
}
```



##### datatranslate拥有足够的开放性与便利性，你甚至可以制作功能更强大的翻译器，让你的同事对你刮目相看！
