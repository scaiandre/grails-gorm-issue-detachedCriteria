import detachedcriteria.Author
import detachedcriteria.Book
import grails.orm.HibernateCriteriaBuilder
import groovy.util.logging.Commons
import org.grails.datastore.gorm.query.criteria.DetachedAssociationCriteria
import org.hibernate.criterion.Subqueries
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Commons
@RestController
@RequestMapping("/detachedCriteria")
class DetachedCriteriaController {
    @GetMapping("/")
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
                    // This causes the queryException
                    property 'alias_author.id'
                }
            })
        }
    }

    @GetMapping("/hibernateException")
    String hibernateException() {
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
                }
                // This causes the hibernateException
                hibernateCriteriaBuilder1.eq "alias_author.id", 1L
            })
        }
    }

    @GetMapping("/worksFine")
    String worksFine() {
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
                }
            })
        }
    }


    @GetMapping("/usingWorkaround")
    String usingWorkaround() {
        final authors = Author.createCriteria().listDistinct {
            HibernateCriteriaBuilder hibernateCriteriaBuilder = delegate as HibernateCriteriaBuilder
            hibernateCriteriaBuilder.projections {
                hibernateCriteriaBuilder.property "id"
            }
            propertyInHelper hibernateCriteriaBuilder, "id", new grails.gorm.DetachedCriteria(Book, "alias_books").build({
                grails.gorm.DetachedCriteria hibernateCriteriaBuilder1 = delegate as grails.gorm.DetachedCriteria
                hibernateCriteriaBuilder1.createAlias("author", "alias_author")
                hibernateCriteriaBuilder1.projections {
                    // No exception due to this
                    property 'alias_author.id'
                }
                // No exception due to this
                hibernateCriteriaBuilder1.eq "alias_author.id", 1L
            })
        }
    }

    static void propertyInHelper(grails.orm.HibernateCriteriaBuilder hibernateCriteriaBuilder, String propertyIn, grails.gorm.DetachedCriteria detachedCriteria) {
        if (detachedCriteria.associationCriteriaMap.size() > 0) {
            final assocationMap = [:]
            for (detachedAssociationCriteriaEntry in detachedCriteria.associationCriteriaMap.entrySet()) {
                assocationMap[detachedAssociationCriteriaEntry.value.associationPath] = detachedAssociationCriteriaEntry.value.alias
            }
            detachedCriteria.associationCriteriaMap.clear()
            detachedCriteria.criteria.removeAll({
                detachedCriteria.criteria.findAll {
                    it instanceof DetachedAssociationCriteria
                }
            })
            final hibernateDetachedCriteria = hibernateCriteriaBuilder.convertToHibernateCriteria(detachedCriteria)
            for (detachedAssociationCriteriaEntry in assocationMap) {
                hibernateDetachedCriteria.createAlias(detachedAssociationCriteriaEntry.key, detachedAssociationCriteriaEntry.value)
            }
            hibernateCriteriaBuilder.add Subqueries.propertyIn(propertyIn, hibernateDetachedCriteria)
        } else {
            hibernateCriteriaBuilder.inList(propertyIn, detachedCriteria)
        }
    }

}