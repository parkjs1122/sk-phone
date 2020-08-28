package skphone.domain;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Store implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String id;
	
	@NotBlank(message = "Store name is required")
	private String storename;
	@NotBlank(message = "Store phone number is required")
	@Indexed(unique = true)
	private String phoneNumber;
	@Size(min = 1, message = "You must upload at least 1 photo")
	private List<String> photo;
	@NotBlank(message = "Location is required")
	private GeoJsonPoint location;
	private String address;
	private Category category;

	public static enum Category {
		RESTAURANT, MARKET, SCHOOL, ACADEMY, ETC
	}
}
