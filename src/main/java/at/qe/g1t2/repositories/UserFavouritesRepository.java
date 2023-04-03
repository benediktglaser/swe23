package at.qe.g1t2.repositories;

import at.qe.g1t2.model.UsersFavourites;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserFavouritesRepository extends JpaRepository<UsersFavourites, UUID> {

}
