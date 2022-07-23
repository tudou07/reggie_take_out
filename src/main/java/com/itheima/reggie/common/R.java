package com.itheima.reggie.common;

import lombok.Data;
import java.util.HashMap;
import java.util.Map;

@Data
public class R<T> {

    private Integer code; //编码：1成功，0和其它数字为失败

    private String msg; //错误信息

    private T data; //数据

    private Map map = new HashMap(); //动态数据

    //静态方法无法访问类上定义的泛型，必须将泛型定义在方法上，即static后的<T>
    public static <T> R<T> success(T object) {
        R<T> r = new R<T>();
        r.data = object;
        r.code = 1;
        return r;
    }

    public static <T> R<T> error(String msg) {
        R r = new R();
        r.msg = msg;
        r.code = 0;
        return r;
    }

    public R<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }

    //测试两种方式定义
/*    public static <T> R<T> put(String key, Object value){
        R r = new R();
        r.map.put(key,value);
        return r;
    }

    public R<T> normal(String meg){
        this.msg = meg;
        this.code = 0;
        return this;
    }*/

}
