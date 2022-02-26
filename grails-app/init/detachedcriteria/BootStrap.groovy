package detachedcriteria

class BootStrap {

    def init = { servletContext ->
        Author.withNewSession {
            Author.withNewTransaction {
                Author author1 = new Author(authorName: "author1")
                author1.save(flush:true)
                Book book1 = new Book(bookName: "book1", author: author1)
                book1.save(flush:true)
                Book book2 = new Book(bookName: "book2", author: author1)
                book2.save(flush:true)
            }
        }
    }
    def destroy = {
    }
}
