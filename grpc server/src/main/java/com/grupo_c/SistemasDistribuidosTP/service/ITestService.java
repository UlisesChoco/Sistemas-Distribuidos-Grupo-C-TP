package com.grupo_c.SistemasDistribuidosTP.service;

import com.grupoc.test.HelloGrpc;
import com.grupoc.test.Test;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

@Service
public class ITestService extends HelloGrpc.HelloImplBase {

    @Override
    public void saludar(Test.Empty request, StreamObserver<Test.SaludoReply> responseObserver){
        String mensaje = "hola mundo!!";
        Test.SaludoReply respuesta = Test.SaludoReply.newBuilder().setSaludo(mensaje).build();

        responseObserver.onNext(respuesta);
        responseObserver.onCompleted();
    }

    @Override
    public void saludarNombre(Test.SaludoRequest request, StreamObserver<Test.SaludoReply> responseObserver){
        String mensaje = "hola " + request.getNombre() + "!!";
        Test.SaludoReply respuesta = Test.SaludoReply.newBuilder().setSaludo(mensaje).build();

        responseObserver.onNext(respuesta);
        responseObserver.onCompleted();
    }
}
