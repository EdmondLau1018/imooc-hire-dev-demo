package pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data // 可以给属性生成 setter getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Stu {
    private Integer id;
    private String name;
    private String sex;
}
