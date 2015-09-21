package de.niko.examples.spring.datarestdemo;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * At runtime, Spring Data REST will create an implementation of this interface automatically. 
 * Then it will use the @RepositoryRestResource annotation to direct Spring MVC to create RESTful endpoints at /people.
 * 
 * @RepositoryRestResource is not required for a repository to be exported. It is only used to change the export details, 
 * such as using /people instead of the default value of /persons. 
 * @author niko
 *
 */
//@RepositoryRestResource(collectionResourceRel = "people", path = "people")
public interface PersonRepository extends PagingAndSortingRepository<Person, Long> {

	List<Person> findByLastName(@Param("name") String name);

}
