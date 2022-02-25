import detachedcriteria.Author
import detachedcriteria.Book
import grails.orm.HibernateCriteriaBuilder
import groovy.util.logging.Commons
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Commons
@RestController
@RequestMapping("/detachedCriteria")
class DetachedCriteriaController {
    @GetMapping
    String index() {
        final authors = Author.createCriteria().listDistinct {
            HibernateCriteriaBuilder hibernateCriteriaBuilder = delegate as HibernateCriteriaBuilder
            hibernateCriteriaBuilder.projections {
                hibernateCriteriaBuilder.property "id"
            }
            hibernateCriteriaBuilder.inList "id", new grails.gorm.DetachedCriteria(Book, "alias_books").build({
                grails.gorm.DetachedCriteria hibernateCriteriaBuilder1 = delegate as grails.gorm.DetachedCriteria
                hibernateCriteriaBuilder1.createAlias("author", "alias_author")
                hibernateCriteriaBuilder1.projections {
                    property 'author.id'
//                    property 'alias_author.id'
                }
                hibernateCriteriaBuilder1.eq "alias_author.id", 1
            })
        }
    }
}