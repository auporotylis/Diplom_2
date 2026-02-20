import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ingredient {
    @JsonProperty("_id")
    private String id;
    private String name;
    private String type;
    private String price;
    private String proteins;
    private String fat;
    private String carbohydrates;
    private String calories;
    private String image;
    @JsonProperty("image_mobile")
    private String imageMobile;
    @JsonProperty("image_large")
    private String imageLarge;
    @JsonProperty("__v")
    private String v;
}
