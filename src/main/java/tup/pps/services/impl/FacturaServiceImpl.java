package tup.pps.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tup.pps.entities.FacturaEntity;
import tup.pps.repositories.FacturaRepository;
import tup.pps.services.FacturaService;
import tup.pps.services.FormaPagoService;
import tup.pps.services.ReparacionService;
import tup.pps.services.SedeService;

import java.util.Optional;

@Service
public class FacturaServiceImpl implements FacturaService {

    @Autowired
    private FacturaRepository repository;

    @Autowired
    private ReparacionService reparacionService;

    //@Autowired
    //private DetalleFacturaService detalleFacturaService;

    @Autowired
    private SedeService sedeService;

    @Autowired
    private FormaPagoService formaPagoService;

    @Autowired
    private ModelMapper modelMapper;

    public Optional<FacturaEntity> findEntityById(Long id) {
        return repository.findById(id);
    }
}
