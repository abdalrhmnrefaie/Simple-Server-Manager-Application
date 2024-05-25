package com.server.server.service.implementation;

import com.server.server.model.Server;
import com.server.server.repo.ServerRepo;
import com.server.server.service.ServerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Collection;
import java.util.Random;

import static com.server.server.enummeration.Status.SERVER_DOWN;
import static com.server.server.enummeration.Status.SERVER_UP;
import static java.lang.Boolean.*;
import static org.springframework.data.domain.PageRequest.*;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class ServerServiceImpl implements ServerService {
    private final ServerRepo serverRepo;
    @Override
    public Server create(Server server) {
        log.info("Saving New Server: {}",server.getName());
        server.setImageUrl(setServerImageUrl());
        return serverRepo.save(server);
    }



    @Override
    public Server ping(String ipAddress) throws IOException {
        log.info("Pinging Server IP: {}",ipAddress);
        Server server=serverRepo.findByIpAddress(ipAddress);
        InetAddress address=InetAddress.getByName(ipAddress);
        server.setStatus(address.isReachable(10000)? SERVER_UP:SERVER_DOWN);
        serverRepo.save(server);
        return server;
    }

    @Override
    public Collection<Server> list(int limit) {
        log.info("Fetching All Servers");
        return serverRepo.findAll(of(0,limit)).toList();
    }

    @Override
    public Server get(Long id) {
        log.info("Fetching Server By Id: {}",id);
        return serverRepo.findById(id).get();
    }

    @Override
    public Server update(Server server) {
        log.info("Updating Server: {}",server.getName());
        return serverRepo.save(server);
    }

    @Override
    public Boolean delete(Long id) {
        log.info("Deleting Server By Id: {}",id);
        serverRepo.deleteById(id);
        return TRUE;
    }

    private String setServerImageUrl() {
        String[] imageNames={"server1.png","server2.png","server3.png","server4.png"};
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/server/image/"+imageNames[new Random().nextInt(4)]).toUriString();
    }

    private  boolean isReachable(String ipAddress,int port,int timeOut){

        try {
            try (Socket socket=new Socket()){
                socket.connect(new InetSocketAddress(ipAddress,port),timeOut);
            }
            return true;

        }catch (IOException exception){

            return false;
        }

    }
}
