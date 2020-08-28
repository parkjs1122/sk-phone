package skphone.domain;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Spam implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	private User user;
	@NotBlank(message = "Spam phone number is required")
	private String phoneNumber;
	@NotBlank(message = "Description is required")
	private String description;
	@NotBlank(message = "Like type is required")
	private LikeType likeType;
	private long likeCount;
	private long dislikeCount;

	public static enum LikeType {
		LIKE, DISLIKE
	}
	
	public Spam withCount(long likeCount, long dislikeCount) {
		return new Spam(id, user, phoneNumber, description, likeType, likeCount, dislikeCount);
	}
}
