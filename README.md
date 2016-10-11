# greenDAO3 数据库配置、增删改查、升级

[![Build Status](https://travis-ci.org/jp1017/GreenDAO3_Demo.svg?branch=master)](https://travis-ci.org/jp1017/GreenDAO3_Demo)

## 配置
---

**喜讯**：现在greenDAO升级到了3.0版本，不需要java项目了

强烈建议升级到3.0版本，该版本采用注解的方式通过编译生成Java数据对象和DAO对象，配置更简单

之前2.0版本的配置也写过，参考这里：[Android SQLite ORM框架greenDAO在Android Studio中的配置与使用](https://jp1017.github.io/2015/12/20/Android-SQLite-ORM%E6%A1%86%E6%9E%B6greenDAO%E5%9C%A8Android-Studio%E4%B8%AD%E7%9A%84%E9%85%8D%E7%BD%AE%E4%B8%8E%E4%BD%BF%E7%94%A8/)

### 1 添加依赖
---

项目的gradle脚本：

```
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:2.2.1'
        classpath 'org.greenrobot:greendao-gradle-plugin:3.1.0'
    }
}
```

module的gradle：

```
apply plugin: 'com.android.application'
apply plugin: 'org.greenrobot.greendao'

android {
    compileSdkVersion 24
    buildToolsVersion "24.0.3"

    defaultConfig {
        applicationId "com.inst.greendao3_demo"
        minSdkVersion 14
        targetSdkVersion 24
        versionCode 1
        versionName "1.0"
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
}

//greendao配置
greendao {
    schemaVersion 1                             //版本号，升级时可配置
    daoPackage'com.inst.greendao3_demo.dao'     //包名
    targetGenDir'src/main/java'                 //生成目录
}

dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    compile 'org.greenrobot:greendao:3.1.1'
}
```

可以看到，3.0多了个greendao插件，直接在这里配置实体及DAO生成目录，我的配置是：

```
greendao {
    schemaVersion 1                             //版本号，升级时可配置
    daoPackage'com.inst.greendao3_demo.dao'     //包名
    targetGenDir'src/main/java'                 //生成目录
}
```

这三个字段的意思是：

>schemaVersion： 数据库schema版本，可在这里升级数据库版本
 daoPackage：设置DaoMaster、DaoSession、Dao包名
 targetGenDir：设置DaoMaster、DaoSession、Dao目录

配置好后，同步下，然后编译，就会生成 Dao。

### 2 添加实体类
---

我这里的是Student实体类，和普通的bean有个区别，添加 `@Entity` 注解

```
@Entity
public class Student {

    @Id
    public Long id;
    public String name;
    public String age;
    public String number;
    public String score;
}
```

注意到，变量 `id` 添加了 @Id 注解，这个就是主键了

### 生成DAO
---

上面配置好后，同步，编译，即可自动生成DAO, 并自动补全实体类 Student 的getter setter 等方法。

## 增删改查
---

上面的操作就完成了数据库的创建，下面开始对数据库操作，常用的增删改查。

这里对基本操作封装下，参考这里：[Android ORM系列之GreenDao最佳实践](http://blog.inet198.cn/?sbsujjbcy/article/details/48156683)

1 编写一个核心辅助类DbCore, 用于获取DaoMaster和DaoSession


```
public static DaoMaster getDaoMaster() {
    if (daoMaster == null) {
        //此处不可用 DaoMaster.DevOpenHelper, 那是开发辅助类，我们要自定义一个，方便升级
        DaoMaster.OpenHelper helper = new MyOpenHelper(mContext, DB_NAME);
        daoMaster = new DaoMaster(helper.getWritableDatabase());
    }
    return daoMaster;
}
```

这里需要注意的是 getDaoMaster 时的 helper 不可用 DaoMaster.DevOpenHelper，我们需要自定义一个：

因为该类这样提示我们：

>/** WARNING: Drops all table on Upgrade! Use only during development. */
     public static class DevOpenHelper extends OpenHelper

也就是开发中使用的助手，自定义也很简单：

```
public class MyOpenHelper extends DaoMaster.OpenHelper {
    public MyOpenHelper(Context context, String name) {
        super(context, name);
    }
}
```

2 基础的泛型 BaseDbHelper, 封装基本增删改查方法，具体看代码吧

3 实现类，有几个实体类就有几个实现类，这里是 StudentHelper, 仅仅一个构造方法即可

4 一个工具类 DbUtils 获得 Helper

5 在 application 里初始化

```
public class DaoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //初始化数据库
        DbCore.init(this);
    }
}
```

6 测试

方法就很简单了，获取 helper， 然后 `add` `remove` `update` `query`

```
StudentHelper mHelper = DbUtil.getDriverHelper();
//增加一个数据
mFastAdapter.add(stu);
//删除一个数据
mFastAdapter.remove(stu);
//更新一个数据
mFastAdapter.update(stu);
//查找所有数据
mFastAdapter.queryAll();
//查找数据库中age大于20的数据
Query<Student> query = mHelper.queryBuilder()
    .where(StudentDao.Properties.Age.ge("20"))
    .build();
dbStudents = query.list();
```

运行程序，添加几个数据，我们看下界面及数据库内容：

<img src="http://7xlah4.com1.z0.glb.clouddn.com/2016-10-11-16-06-13-526_com.inst.green.png" width="320"/> <img src="http://7xlah4.com1.z0.glb.clouddn.com/2016-10-11-16-06-26-262_com.speedsoftw.png" width="320"/>

## 升级
---

今天研究了下升级，掌握方法了还是蛮简单的，这里对数据库的升级，仅仅是添加字段，添加表。对于删除，修改字段这里不多讲，因为sqlite数据库不适合此操作：

>SQLite supports a limited subset of ALTER TABLE. The ALTER TABLE command in SQLite allows the user to rename a table or to add a new column to an existing table. It is not possible to rename a column, remove a column, or add or remove constraints from a table.

简单讲就是 SQlite 数据库仅能重命名表及增加字段，其他不支持，如果您一定要操作，也是可以的，来这里吧：

[SQLite如何删除，修改、重命名列](http://www.2cto.com/database/201110/106835.html)

### 1 升级版本号
---

上面我们说到，在 gradle 里修改 schemaVersion 即可，现在我们设置为2，编译下，我们可以看到 DaoMaster 里的schema变为2：

>public static final int SCHEMA_VERSION = 2;

### 2 实体添加字段
---

比如我们的 Student 添加一个 score 字段， 这个可以直接写到 Student 里：

```
public String score;
```

编译后即可生成完整的 Student 实体及 DAO

### 3重写onUpgrade方法升级
---

这个就是重写 MyOpenHelper 的 onUpgrade 方法，该方法只在 schema 升级时执行一次.

在该方法里添加 score 字段即可

```
@Override
public void onUpgrade(Database db, int oldVersion, int newVersion) {
    KLog.w("db version update from " + oldVersion + " to " + newVersion);

    switch (oldVersion) {
        case 1:
            //不能先删除表，否则数据都木了
//                StudentDao.dropTable(db, true);
            StudentDao.createTable(db, true);
            // 加入新字段 score
            db.execSQL("ALTER TABLE 'STUDENT' ADD 'SCORE' TEXT;");

            break;
    }

}
```

### 4 测试
---

运行代码后，再添加三个数据，我们看下界面及数据库内容：

<img src="http://7xlah4.com1.z0.glb.clouddn.com/2016-10-11-16-15-21-621_com.inst.green.png" width="320"/> <img src="http://7xlah4.com1.z0.glb.clouddn.com/2016-10-11-16-15-31-503_com.speedsoftw.png" width="320"/>

代码已上传至 GitHub：[GreenDAO3_Demo](https://github.com/jp1017/GreenDAO3_Demo/tree/update)