package com.grupo_c.SistemasDistribuidosTP.service;

import com.grupoc.test.Test;
import com.grupoc.test.UsersGrpc;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

@Service
public class IUsuarioService extends UsersGrpc.UsersImplBase {

    @Override
    public void devolverUsuario(Test.Empty request, StreamObserver<Test.UsuarioDto> responseObserver){
        Test.UsuarioDto respuesta = Test.UsuarioDto.newBuilder()
                .setName("Nombre").setLastName("Apellido").setAge(30).setEmail("emailrandom@gmail.com")
                .build();
        responseObserver.onNext(respuesta);
        responseObserver.onCompleted();
    }
}
