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
import skphone.data.StoreRepository;
import skphone.domain.Store;
import skphone.utils.reactive.CacheUtil;

@RestController
@RequestMapping(path = "/store", produces = "application/json")
@CrossOrigin(origins = "*")
public class StoreController {
	private StoreRepository storeRepo;
	private CacheManager cacheManager;
	
	@Autowired
	public StoreController(StoreRepository storeRepo, CacheManager cacheManager) {
		this.storeRepo = storeRepo;
		this.cacheManager = cacheManager;
	}

	@GetMapping("/{phoneNumber}")
	public Mono<Store> getStore(@PathVariable("phoneNumber") String phoneNumber) {
		return CacheUtil.cacheMono(
			cacheManager,
			"store",
			phoneNumber,
			storeRepo.findByPhoneNumber(phoneNumber),
			Store.class
		);
	}

	@PostMapping(consumes = "application/json")
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<Store> saveStore(@RequestBody Mono<Store> storeMono) {
		return storeRepo.saveAll(storeMono).next();
	}

	@PatchMapping(path = "/{phoneNumber}", consumes = "application/json")
	public Mono<Store> updateStore(@PathVariable("phoneNumber") String phoneNumber, @RequestBody Store patch) {
		return storeRepo.findByPhoneNumber(phoneNumber).map(store -> {
			if (patch.getStorename() != null) {
				store.setStorename(patch.getStorename());
			}
			if (patch.getAddress() != null) {
				store.setAddress(patch.getAddress());
			}
			if (patch.getCategory() != null) {
				store.setCategory(patch.getCategory());
			}
			return store;
		}).flatMap(storeRepo::save);
	}

	@DeleteMapping("/{phoneNumber}")
	public void deleteStore(@PathVariable("phoneNumber") String phoneNumber) {
		storeRepo.deleteByPhoneNumber(phoneNumber);
	}
}
