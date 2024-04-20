package br.com.rocha.apiboleto.repository;

import br.com.rocha.apiboleto.entity.BoletoEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoletoRepository extends CrudRepository<BoletoEntity, Long> {
    Optional<BoletoEntity> findByCodigoBarras(String codigoBarras);
}
