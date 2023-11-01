package kr.co.playplace.service.radar;

import ch.hsr.geohash.GeoHash;
import kr.co.playplace.controller.radar.response.UsersNearbyResponse;
import kr.co.playplace.entity.song.Song;
import kr.co.playplace.entity.user.Users;
import kr.co.playplace.repository.location.UserLocationRepository;
import kr.co.playplace.controller.radar.request.UserLocationRequest;
import kr.co.playplace.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.*;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.redis.connection.RedisGeoCommands.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RadarService {

    private final UserRepository userRepository;
    private final UserLocationRepository userLocationRepository;

    private final RedisTemplate redisTemplate;

    public List<UsersNearbyResponse> findUsersNearby(long userId, double longitude, double latitude) {

//        GeoHash geoHash = GeoHash.withCharacterPrecision(latitude, longitude, 7);
//        GeoHash[] adjHash = geoHash.getAdjacent();
//        log.debug("location : {}", geoHash.toBase32());
//        for(GeoHash adj : adjHash) {
//            log.debug("adjLocation : {}",adj.toBase32());
//        }

        // 사용자의 위치를 중심으로 반경 100m 범위 설정
        Point userLocation = new Point(longitude, latitude);
        Distance radius = new Distance(100, DistanceUnit.METERS);
        Circle circle = new Circle(userLocation, radius);

        // redis geo 자료구조 설정
        GeoOperations<String, String> geoOperations = redisTemplate.opsForGeo();

        // redis geo 명령어 옵션 설정, limit 30
        GeoRadiusCommandArgs args = GeoRadiusCommandArgs
                .newGeoRadiusArgs()
                .includeDistance()
                .includeCoordinates()
                .sortAscending()
                .limit(15);

        // 설정한 위치 범위 안에 있는 사용자들 검색
        GeoResults<GeoLocation<String>> results = geoOperations
                .radius("geoPoints", circle, args);

        List<UsersNearbyResponse> list = new ArrayList<>();

        // 범위 안에 있는 사용자에 대해 최근 재생 곡 조회
       for(GeoResult<GeoLocation<String>> result : results) {
           GeoLocation<String> location = result.getContent();

           Long userNearbyId = Long.parseLong(location.getName());

           // 본인은 제외
           if(userNearbyId == userId) {
               continue;
           }

           log.debug("userNearby:{}", userNearbyId);

           Users user = userRepository.findById(userNearbyId).get();

           // TODO: 사용자 최신 재생 곡 정보 가져오기
           Song song = new Song(1L, "youtube", "title", "artist", "img", 1);

           UsersNearbyResponse usersNearbyResponse = UsersNearbyResponse.of(user, song, 0);

           list.add(usersNearbyResponse);
       }

        return list;

    }

    public void saveUserLocationTest(long userId, UserLocationRequest userLocationRequest) {
//        UserLocation userLocation = UserLocation.builder()
//                .id(userId)
//                .longitude(userLocationRequest.getLongitude())
//                .latitude(userLocationRequest.getLatitude())
//                .songId(1L)
//                .build();

//        userLocationRepository.save(userLocation);

//        log.debug("latitude: {}",userLocationRepository.findById(userId).map(UserLocation::getLatitude).orElse(0.0));

//        String key = GeoHash.withCharacterPrecision(userLocationRequest.getLatitude(), userLocationRequest.getLongitude(), 7).toBase32();

        // redis geo 자료구조 설정
        GeoOperations<String, String> geoOperations = redisTemplate.opsForGeo();

        String key = "geoPoints";
        Point point = new Point(userLocationRequest.getLongitude(), userLocationRequest.getLatitude());

        // redis에 geohash 별로 저장
        geoOperations.add(key, point, "" + userId);

        log.debug("geoHash: {}", geoOperations.hash(key,"" + userId));
    }
}
