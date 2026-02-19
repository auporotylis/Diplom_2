import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ingredient {
    private String _id;
    private String name;
    private String type;
    private String price;
    private String proteins;
    private String fat;
    private String carbohydrates;
    private String calories;
    private String image;
    private String image_mobile;
    private String image_large;
    private String __v;
}
