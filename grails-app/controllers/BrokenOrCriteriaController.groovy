import detachedcriteria.Author
import grails.orm.HibernateCriteriaBuilder
import groovy.util.logging.Commons
import org.hibernate.criterion.Restrictions
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Commons
@RestController
@RequestMapping("/brokenMixingOfHibernateCriterionWithFormCriteria")
class BrokenOrCriteriaController {

    @GetMapping("/")
    String brokenMixingOfHibernateCriterionWithFormCriteria() {
        final authors = Author.createCriteria().listDistinct {
            HibernateCriteriaBuilder hibernateCriteriaBuilder = delegate as HibernateCriteriaBuilder
            hibernateCriteriaBuilder.projections {
                hibernateCriteriaBuilder.property "id"
            }
            or {
                add Restrictions.eq("id", 1L)
                eq "authorName", "author1"
            }
        }
    }

}