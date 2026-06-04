package cl.duoc.mineria.ciclo_transporte.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import cl.duoc.mineria.ciclo_transporte.model.CicloTransporte;

@Repository
public interface CicloTransporteRepository extends JpaRepository<CicloTransporte, Long> {
    
    List<CicloTransporte> findByCamionId(Long camionId);

    List<CicloTransporte> findByTurnoId(Long turnoId);
}
