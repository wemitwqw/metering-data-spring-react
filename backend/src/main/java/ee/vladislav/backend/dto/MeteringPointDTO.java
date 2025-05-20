package ee.vladislav.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeteringPointDTO {
		private Long id;
		private String address;
		private String meterId;
}
