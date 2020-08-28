package skphone.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;
import skphone.data.SpamRepository;
import skphone.domain.Spam;
import skphone.domain.Spam.LikeType;
import skphone.utils.reactive.CacheUtil;

@RestController
@RequestMapping(path = "/spam", produces = "application/json")
@CrossOrigin(origins = "*")
public class SpamController {
	private SpamRepository spamRepo;
	private CacheManager cacheManager;
	private long likeCnt, dislikeCnt;
	
	@Autowired
	public SpamController(SpamRepository spamRepo, CacheManager cacheManager) {
		this.spamRepo = spamRepo;
		this.cacheManager = cacheManager;
	}
	
	@GetMapping("/{phoneNumber}")
	public Mono<Spam> getSpam(@PathVariable("phoneNumber") String phoneNumber) {
		return CacheUtil.cacheMono(
			cacheManager,
			"spam",
			phoneNumber,
			Mono.defer(() -> spamRepo.findFirstByPhoneNumber(phoneNumber).map(spam -> {
				spamRepo.countByPhoneNumberAndLikeType(phoneNumber, LikeType.LIKE).subscribe(v -> likeCnt = v);
				spamRepo.countByPhoneNumberAndLikeType(phoneNumber, LikeType.DISLIKE).subscribe(v -> dislikeCnt = v);
				return spam.withCount(likeCnt, dislikeCnt);
			})),
			Spam.class
		);
	}

	@PostMapping(consumes = "application/json")
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<Spam> saveSpam(@RequestBody Mono<Spam> spamMono) {
		return spamRepo.saveAll(spamMono).next();
	}

	@PatchMapping(path = "/{phoneNumber}", consumes = "application/json")
	public Mono<Spam> updateSpam(@PathVariable("phoneNumber") String phoneNumber, @RequestBody Spam patch) {
		return spamRepo.findByUserAndPhoneNumber(null, phoneNumber).map(spam -> {
			if (patch.getDescription() != null) {
				spam.setDescription(patch.getDescription());
			}
			return spam;
		}).flatMap(spamRepo::save);
	}

	@DeleteMapping("/{phoneNumber}")
	public void deleteSpam(@PathVariable("phoneNumber") String phoneNumber) {
		spamRepo.deleteByPhoneNumber(phoneNumber);
	}

}
