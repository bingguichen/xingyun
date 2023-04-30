import cn.hutool.core.util.ClassUtil;
import com.lframework.starter.common.utils.StringUtil;
import com.lframework.starter.web.enums.BaseEnum;
import com.lframework.starter.web.utils.JsonUtil;
import com.lframework.xingyun.sc.enums.StockAdjustSheetBizType;
import com.lframework.xingyun.sc.enums.StockAdjustSheetStatus;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Test;

public class EnumTest {

  /**
   * 这是一个根据Enum类生成前端所需要的Enum
   */
  public static void main(String[] args) {
    Map<Object, Object> map = new LinkedHashMap<>();
    Class clazz = StockAdjustSheetStatus.class;

    BaseEnum[] objs = ClassUtil.invoke(clazz.getName() + "#values", new Object[0]);

    for (BaseEnum value : objs) {
      Map<Object, Object> map1 = new LinkedHashMap<>();
      map1.put("code", value.getCode());
      map1.put("desc", value.getDesc());
      map.put(((Enum) value).name(), map1);
    }

    String className = StringUtil.toUnderlineCase(clazz.getSimpleName()).toUpperCase();
    System.out.println(className.toLowerCase().replaceAll("_", "-") + ".js");
    System.out.println(StringUtil.format("const {} = {} \n\nexport default {}", className,
        JsonUtil.toJsonPrettyStr(map).replaceAll("\"(\\w+)\"(\\s*:\\s*)", "$1$2")
            .replaceAll("\"", "'"), className));
  }
}
