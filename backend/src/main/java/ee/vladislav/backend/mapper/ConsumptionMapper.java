package ee.vladislav.backend.mapper;

import ee.vladislav.backend.dao.Consumption;
import ee.vladislav.backend.dto.ConsumptionDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {MeteringPointMapper.class})
public interface ConsumptionMapper {

	@Mapping(target = "amount", source = "amount")
	@Mapping(target = "amountUnit", source = "amountUnit")
	@Mapping(target = "consumptionTime", source = "consumptionTime")
	@Mapping(target = "createdAt", source = "createdAt")
	ConsumptionDTO consumptionToConsumptionDTO(Consumption consumption);
}