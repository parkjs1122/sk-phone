package skphone.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;
import skphone.data.UserRepository;
import skphone.domain.User;
import skphone.utils.reactive.CacheUtil;

@RestController
@RequestMapping(path = "/user", produces = "application/json")
@CrossOrigin(origins = "*")
public class UserController {
	private UserRepository userRepo;
	private CacheManager cacheManager;

	@Autowired
	public UserController(UserRepository userRepo, CacheManager cacheManager) {
		this.userRepo = userRepo;
		this.cacheManager = cacheManager;
	}
	
	@GetMapping("/{phoneNumber}")
	public Mono<User> getUser(@PathVariable("phoneNumber") String phoneNumber) {
		return CacheUtil.cacheMono(
			cacheManager,
			"user",
			phoneNumber,
			userRepo.findByPhoneNumber(phoneNumber),
			User.class
		);
	}

	@PostMapping(consumes = "application/json")
	public Mono<User> saveUser(@RequestBody User user) {
		return userRepo.save(user);
	}

	@PatchMapping(path = "/{phoneNumber}", consumes = "application/json")
	public Mono<User> updateUser(@PathVariable("phoneNumber") String phoneNumber, @RequestBody User patch) {
		return userRepo.findByPhoneNumber(phoneNumber).map(user -> {
			// if (patch.getStorename() != null) {
			// user.setStorename(patch.getStorename());
			// }
			return user;
		}).flatMap(userRepo::save);
	}

	@DeleteMapping("/{phoneNumber}")
	public void deleteUser(@PathVariable("phoneNumber") String phoneNumber) {
		userRepo.deleteByPhoneNumber(phoneNumber);
	}
}
