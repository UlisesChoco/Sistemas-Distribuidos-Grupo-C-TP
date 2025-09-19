package com.grupo_c.SistemasDistribuidosTP.serviceImp;

import com.grupo_c.SistemasDistribuidosTP.entity.Inventory;
import com.grupo_c.SistemasDistribuidosTP.entity.User;
import com.grupo_c.SistemasDistribuidosTP.mapper.InventoryMapper;
import com.grupo_c.SistemasDistribuidosTP.repository.IInventoryRepository;
import com.grupo_c.SistemasDistribuidosTP.repository.IUserRepository;
import com.grupo_c.SistemasDistribuidosTP.service.IInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryServiceImpl implements IInventoryService {

    private final IInventoryRepository inventoryRepository;
    private final IUserRepository userRepository;
    private final InventoryMapper inventoryMapper;

    @Autowired
    public InventoryServiceImpl(IInventoryRepository inventoryRepository,
                                IUserRepository userRepository) {
        this.inventoryRepository = inventoryRepository;
        this.userRepository = userRepository;
        this.inventoryMapper = new InventoryMapper();
    }

    @Override
    public List<Inventory> findAll() {
        return inventoryRepository.findAll();
    }

    @Override
    public Inventory findById(Long id) {
        return inventoryRepository.findById(id).orElse(null);
    }

    @Override
    public List<Inventory> findByCategory(Inventory.Category category) {
        return inventoryRepository.findByCategory(category);
    }

    @Override
    public List<Inventory> findByisDeleted(Boolean isDeleted) {
        return isDeleted ?
                inventoryRepository.findByisDeletedTrue() :
                inventoryRepository.findByisDeletedFalse();
    }

    @Override
    public List<Inventory> findByDescriptionContaining(String description) {
        return inventoryRepository.findByDescriptionContainingIgnoreCase(description);
    }

    @Override
    public Inventory save(Inventory inventory, User currentUser) {
        if (inventory.getId() == null) {
            inventory.setUserCreator(currentUser);
            inventory.setCreationDate(java.time.LocalDateTime.now());
        }
        inventory.setUserModify(currentUser);
        inventory.setModificationDate(java.time.LocalDateTime.now());
        return inventoryRepository.save(inventory);
    }

    @Override
    public void deleteById(Long id, User currentUser) {
        inventoryRepository.findById(id).ifPresent(inventory -> {
            inventory.setisDeleted(true);
            inventory.setUserModify(currentUser);
            inventory.setModificationDate(java.time.LocalDateTime.now());
            inventoryRepository.save(inventory);
        });
    }

    @Override
    public List<Inventory> findAvailableInventory() {
        System.out.println("DEBUG: Se ha invocado findAvailableInventory()");
        return inventoryRepository.findAvailableInventory();
    }

    @Override
    public Inventory decreaseStock(Long id, int quantityToDecrease) {
        // Buscar inventario por id
        Inventory inventory = inventoryRepository.findById(id).orElse(null);

        if (inventory == null) {
            throw new RuntimeException("Inventario no encontrado con id: " + id);
        }

        if (quantityToDecrease <= 0) {
            throw new IllegalArgumentException("La cantidad a descontar debe ser mayor a 0.");
        }

        if (inventory.getQuantity() < quantityToDecrease) {
            throw new RuntimeException("Stock insuficiente en inventario con id: " + id);
        }

        inventory.setQuantity(inventory.getQuantity() - quantityToDecrease);
        // no cambiamos usuario/fechas aquí: es una operación interna. Si querés auditoría, se podría extender.
        return inventoryRepository.save(inventory);
    }
}