package ee.vladislav.backend.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
public class PriceResponse implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	private Integer year;
	private List<MonthlyPriceDTO> months;
}