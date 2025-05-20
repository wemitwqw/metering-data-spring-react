package ee.vladislav.backend.mapper;

import ee.vladislav.backend.dao.MeteringPoint;
import ee.vladislav.backend.dto.MeteringPointDTO;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MeteringPointMapper {
	
	@Mapping(target = "address", source = "address")
	@Mapping(target = "meterId", source = "meterId")
	MeteringPointDTO meteringPointToMeteringPointDTO(MeteringPoint meteringPoint);

	List<MeteringPointDTO> meteringPointsToMeteringPointDTOs(List<MeteringPoint> meteringPoints);
}