package com.grupo_c.SistemasDistribuidosTP.serviceImp;

import com.grupo_c.SistemasDistribuidosTP.entity.Inventory;
import com.grupo_c.SistemasDistribuidosTP.entity.User;
import com.grupo_c.SistemasDistribuidosTP.mapper.InventoryMapper;
import com.grupo_c.SistemasDistribuidosTP.repository.IUserRepository;
import com.grupo_c.SistemasDistribuidosTP.service.InventoryServiceClass;
import com.grupo_c.SistemasDistribuidosTP.service.InventoryServiceGrpc;
import com.grupo_c.SistemasDistribuidosTP.service.UserServiceClass;
import com.grupo_c.SistemasDistribuidosTP.service.UtilsServiceClass;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryServiceGrpcImpl extends InventoryServiceGrpc.InventoryServiceImplBase {

    private final InventoryServiceImpl inventoryService;
    private final IUserRepository userRepository;
    private final InventoryMapper inventoryMapper;

    @Autowired
    public InventoryServiceGrpcImpl(InventoryServiceImpl inventoryService,
                                    IUserRepository userRepository) {
        this.inventoryService = inventoryService;
        this.userRepository = userRepository;
        this.inventoryMapper = new InventoryMapper();
    }

    @Override
    public void getInventoryList(UtilsServiceClass.Empty request,
                                 StreamObserver<InventoryServiceClass.InventoryListResponse> responseObserver) {
        try {
            List<Inventory> inventories = inventoryService.findAll();
            InventoryServiceClass.InventoryListResponse.Builder responseBuilder =
                    InventoryServiceClass.InventoryListResponse.newBuilder();

            for (Inventory inventory : inventories) {
                User userCreator = inventory.getUserCreator();
                User userModify = inventory.getUserModify();

                UserServiceClass.UserSimpleDTO userCreatorDTO = userCreator != null ?
                        inventoryMapper.toUserSimpleDTO(userCreator) :
                        UserServiceClass.UserSimpleDTO.newBuilder().setId(0L).setUsername("").build();

                UserServiceClass.UserSimpleDTO userModifyDTO = userModify != null ?
                        inventoryMapper.toUserSimpleDTO(userModify) :
                        UserServiceClass.UserSimpleDTO.newBuilder().setId(0L).setUsername("").build();

                responseBuilder.addInventories(
                        inventoryMapper.toInventoryDTO(inventory, userCreatorDTO, userModifyDTO)
                );
            }

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(Status.INTERNAL
                    .withDescription("Error al recuperar la lista de inventario: " + e.getMessage())));
        }
    }

    @Override
    public void getInventoryById(InventoryServiceClass.InventoryRequest request,
                                 StreamObserver<InventoryServiceClass.InventoryDTO> responseObserver) {
        try {
            Inventory inventory = inventoryService.findById(request.getIdInventory());
            if (inventory != null) {
                User userCreator = inventory.getUserCreator();
                User userModify = inventory.getUserModify();

                UserServiceClass.UserSimpleDTO userCreatorDTO = userCreator != null ?
                        inventoryMapper.toUserSimpleDTO(userCreator) :
                        UserServiceClass.UserSimpleDTO.newBuilder().setId(0L).setUsername("").build();

                UserServiceClass.UserSimpleDTO userModifyDTO = userModify != null ?
                        inventoryMapper.toUserSimpleDTO(userModify) :
                        UserServiceClass.UserSimpleDTO.newBuilder().setId(0L).setUsername("").build();

                responseObserver.onNext(inventoryMapper.toInventoryDTO(inventory, userCreatorDTO, userModifyDTO));
                responseObserver.onCompleted();
            } else {
                responseObserver.onError(new StatusRuntimeException(Status.NOT_FOUND
                        .withDescription("Inventario no encontrado con id: " + request.getIdInventory())));
            }
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(Status.INTERNAL
                    .withDescription("Error al recuperar el inventario:" + e.getMessage())));
        }
    }

    @Override
    public void createInventory(InventoryServiceClass.InventoryDTO request,
                                StreamObserver<InventoryServiceClass.InventoryDTO> responseObserver) {
        try {
            User currentUser = userRepository.findById(1L).orElse(null);
            if (currentUser == null) {
                responseObserver.onError(new StatusRuntimeException(Status.NOT_FOUND
                        .withDescription("Usuario actual no encontrado")));
                return;
            }

            Inventory inventory = inventoryMapper.toEntity(request, currentUser, currentUser);
            Inventory savedInventory = inventoryService.save(inventory, currentUser);

            UserServiceClass.UserSimpleDTO userCreatorDTO = inventoryMapper.toUserSimpleDTO(currentUser);
            UserServiceClass.UserSimpleDTO userModifyDTO = inventoryMapper.toUserSimpleDTO(currentUser);

            responseObserver.onNext(inventoryMapper.toInventoryDTO(savedInventory, userCreatorDTO, userModifyDTO));
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(Status.INTERNAL
                    .withDescription("Error al crear inventario: " + e.getMessage())));
        }
    }

   @Override
public void updateInventory(InventoryServiceClass.InventoryDTO request,
                            StreamObserver<InventoryServiceClass.InventoryDTO> responseObserver) {
    try {
        User currentUser = userRepository.findById(1L).orElse(null);
        if (currentUser == null) {
            responseObserver.onError(new StatusRuntimeException(Status.NOT_FOUND
                    .withDescription("Usuario actual no encontrado")));
            return;
        }

        Inventory existingInventory = inventoryService.findById(request.getIdInventory());
        if (existingInventory == null) {
            responseObserver.onError(new StatusRuntimeException(Status.NOT_FOUND
                    .withDescription("Inventario no encontrado con id:: " + request.getIdInventory())));
            return;
        }

        // Solo permite modificar descripción y cantidad
        existingInventory.setDescription(request.getDescription());
        existingInventory.setQuantity(request.getQuantity());

        // userModify y fecha modificación
        existingInventory.setUserModify(currentUser);
        existingInventory.setModificationDate(java.time.LocalDateTime.now());

        Inventory updatedInventory = inventoryService.save(existingInventory, currentUser);

        UserServiceClass.UserSimpleDTO userCreatorDTO = inventoryMapper.toUserSimpleDTO(existingInventory.getUserCreator());
        UserServiceClass.UserSimpleDTO userModifyDTO = inventoryMapper.toUserSimpleDTO(currentUser);

        responseObserver.onNext(inventoryMapper.toInventoryDTO(updatedInventory, userCreatorDTO, userModifyDTO));
        responseObserver.onCompleted();
    } catch (Exception e) {
        responseObserver.onError(new StatusRuntimeException(Status.INTERNAL
                .withDescription("Error al actualizar el inventario:" + e.getMessage())));
    }
}

    @Override
    public void deleteInventory(InventoryServiceClass.InventoryRequest request,
                                StreamObserver<UtilsServiceClass.Empty> responseObserver) {
        try {
            User currentUser = userRepository.findById(1L).orElse(null);
            if (currentUser == null) {
                responseObserver.onError(new StatusRuntimeException(Status.NOT_FOUND
                        .withDescription("Usuario actual no encontrado")));
                return;
            }

            inventoryService.deleteById(request.getIdInventory(), currentUser);

            responseObserver.onNext(UtilsServiceClass.Empty.newBuilder().build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(Status.INTERNAL
                    .withDescription("Error al eliminar el inventario:" + e.getMessage())));
        }
    }

     // ===================== NUEVO: devuelve sólo inventarios activos (isDeleted = false) =====================
    @Override
    public void getAvailableInventory(UtilsServiceClass.Empty request,
                                      StreamObserver<InventoryServiceClass.InventoryListResponse> responseObserver) {
        try {
            List<Inventory> inventories = inventoryService.findAvailableInventory();
            InventoryServiceClass.InventoryListResponse.Builder responseBuilder =
                    InventoryServiceClass.InventoryListResponse.newBuilder();

            for (Inventory inventory : inventories) {
                User userCreator = inventory.getUserCreator();
                User userModify = inventory.getUserModify();

                UserServiceClass.UserSimpleDTO userCreatorDTO = userCreator != null ?
                        inventoryMapper.toUserSimpleDTO(userCreator) :
                        UserServiceClass.UserSimpleDTO.newBuilder().setId(0L).setUsername("").build();

                UserServiceClass.UserSimpleDTO userModifyDTO = userModify != null ?
                        inventoryMapper.toUserSimpleDTO(userModify) :
                        UserServiceClass.UserSimpleDTO.newBuilder().setId(0L).setUsername("").build();

                responseBuilder.addInventories(
                        inventoryMapper.toInventoryDTO(inventory, userCreatorDTO, userModifyDTO)
                );
            }

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(Status.INTERNAL
                    .withDescription("Error al recuperar inventarios activos: " + e.getMessage())));
        }
    }
    // ============================================================================================
    @Override
    public void getInventoryByCategory(InventoryServiceClass.InventoryCategory request,
                                       StreamObserver<InventoryServiceClass.InventoryListResponse> responseObserver) {
        try {
            Inventory.Category category = Inventory.Category.valueOf(request.getCategory());
            List<Inventory> inventories = inventoryService.findByCategory(category);

            InventoryServiceClass.InventoryListResponse.Builder responseBuilder =
                    InventoryServiceClass.InventoryListResponse.newBuilder();

            for (Inventory inventory : inventories) {
                User userCreator = inventory.getUserCreator();
                User userModify = inventory.getUserModify();

                UserServiceClass.UserSimpleDTO userCreatorDTO = userCreator != null ?
                        inventoryMapper.toUserSimpleDTO(userCreator) :
                        UserServiceClass.UserSimpleDTO.newBuilder().setId(0L).setUsername("").build();

                UserServiceClass.UserSimpleDTO userModifyDTO = userModify != null ?
                        inventoryMapper.toUserSimpleDTO(userModify) :
                        UserServiceClass.UserSimpleDTO.newBuilder().setId(0L).setUsername("").build();

                responseBuilder.addInventories(
                        inventoryMapper.toInventoryDTO(inventory, userCreatorDTO, userModifyDTO)
                );
            }

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(new StatusRuntimeException(Status.INVALID_ARGUMENT
                    .withDescription("Categoria Invalida " + request.getCategory())));
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(Status.INTERNAL
                    .withDescription("Error al recuperar el inventario por categoría:" + e.getMessage())));
        }
    }
}