package com.hust.soict.socket.service;

import com.hust.soict.socket.SocketApplication;
import com.hust.soict.socket.data_access.UserFriendRepo;
import com.hust.soict.socket.data_access.UserRepo;
import com.hust.soict.socket.domain.User;
import com.hust.soict.socket.domain.UserFriend;
import com.hust.soict.socket.response_object.FriendResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepo userRepo;

    private final UserFriendRepo userFriendRepo;

    public UserService(UserRepo userRepo, UserFriendRepo userFriendRepo) {
        this.userRepo = userRepo;
        this.userFriendRepo = userFriendRepo;
    }

    public User login(String username, String password){
        return userRepo.findByUsernameAndPassword(username,password).orElseThrow(()->new RuntimeException("Bad Credential"));
    }

    public void register(String username, String password, String inGame) throws Exception {
        if (userRepo.existsUserByUsername(username))
            throw new Exception("User name exist");
        if (userRepo.existsUserByInGame(inGame))
            throw new Exception("InGame exist");

        User user = new User(username,password,inGame);
        userRepo.save(user);
    }

    public void acceptFriend(String currentIngame, String ingame) {
        User user1 = userRepo.findByInGame(currentIngame).orElseThrow(()->new RuntimeException("User not found with ingame "+currentIngame));
        User user2 = userRepo.findByInGame(ingame).orElseThrow(()->new RuntimeException("User not found with ingame "+ingame));

        UserFriend userFriend1 = new UserFriend(user1.getId(), user2.getId());
        UserFriend userFriend2 = new UserFriend(user2.getId(), user1.getId());

        userFriendRepo.save(userFriend1);
        userFriendRepo.save(userFriend2);
    }

    public List<FriendResponse> getFriend(String currentIngame) {
        User user = userRepo.findByInGame(currentIngame).orElseThrow(()->new RuntimeException("User not found with ingame "+currentIngame));;
        Long userId = user.getId();

        // find friend with id
        List<UserFriend> idList = userFriendRepo.findByUserId(userId);
        List<Long> result = idList.stream().map(UserFriend::getFriendId).collect(Collectors.toList());
        List<User> frList = userRepo.findByIdIn(result);
        List<FriendResponse> list = new ArrayList<>();

        frList.forEach((item)->{
            boolean isOnline = false;
            if (SocketApplication.activeClient.containsKey(item.getInGame()))
                isOnline = true;
            list.add(new FriendResponse(isOnline,item.getInGame()));
        });
        return list;
    }
}
