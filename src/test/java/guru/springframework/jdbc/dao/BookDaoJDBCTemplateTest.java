package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author E.I.
 * <p>
 * {@code @Date}  3/5/2023
 */
@ActiveProfiles("local")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan(basePackages = {"guru.springframework.jdbc.dao"})
class BookDaoJDBCTemplateTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    BookDao bookDao;

    @BeforeEach
    void setUp() {
        bookDao = new BookDaoJDBCTemplate(jdbcTemplate);
    }

    @Test
    void testFindAllBooksPage1_SortedByTitle() {
        List<Book> books = bookDao.findAllBooksSortByTitle(PageRequest.of(0, 10,
                Sort.by(Sort.Order.desc("title"))));

        assertAll("it should return page1 with size 10",
                () -> assertThat(books).isNotNull(),
                () ->assertThat(books).hasSize(10) );
    }

    @Test
    void testFindAllBooksPageableOne() {
        List<Book> books = bookDao.findAllBooks(PageRequest.of(0, 10));
        assertAll("it should return page1 with size 10",
                () -> assertThat(books).isNotNull(),
                () ->assertThat(books).hasSize(10) );
    }

    @Test
    void testFindAllBooksPageableTwo() {
        List<Book> books = bookDao.findAllBooks(PageRequest.of(1, 10));

        assertAll("",
                () ->assertThat(books).isNotNull(),
                () ->assertThat(books).hasSize(9) );
    }


    @Test
    void testFindAllBooksPageable10() {

        List<Book> books = bookDao.findAllBooks(PageRequest.of(2, 10));
        assertThat(books).isNotNull().isEmpty();
    }

    @Test
    void testFindAllBooksPageOne() {
        List<Book> books = bookDao.findAllBooks(10, 0);

        assertAll("",
                () ->assertThat(books).isNotNull(),
                () ->assertThat(books).hasSize(10) );
    }

    @Test
    void testFindAllBooksPageTwo() {
        List<Book> books = bookDao.findAllBooks(10, 10);

        assertAll("",
                () ->  assertThat(books).isNotNull(),
                () ->assertThat(books).hasSize(9) );
    }


    @Test
    void testFindAllBooksPage10() {
        List<Book> books = bookDao.findAllBooks(10, 100);

        assertThat(books).isNotNull().isEmpty();
    }

    @Test
    void testFindAllBooks() {
        List<Book> books = bookDao.findAllBooks();
        assertAll("",
                () ->assertThat(books).isNotNull(),
                () -> assertThat(books).hasSize(5) );
    }

    @Test
    void testDeleteBook() {
        Book book = new Book();
        book.setIsbn("1234");
        book.setPublisher("Self");
        book.setTitle("my book");
        Book saved = bookDao.saveNewBook(book);

        bookDao.deleteBookById(saved.getId());

        assertThrows(EmptyResultDataAccessException.class, () -> bookDao.getById(saved.getId()));
    }

    @Test
    void updateBookTest() {
        Book book = new Book();
        book.setIsbn("1234");
        book.setPublisher("Self");
        book.setTitle("my book");
        book.setAuthorId(1L);
        Book saved = bookDao.saveNewBook(book);

        saved.setTitle("New Book");
        bookDao.updateBook(saved);

        Book fetched = bookDao.getById(saved.getId());

        assertThat(fetched.getTitle()).isEqualTo("New Book");
    }

    @Test
    void testSaveBook() {
        Book book = new Book();
        book.setIsbn("1234");
        book.setPublisher("Self");
        book.setTitle("my book");
        book.setAuthorId(1L);

        Book saved = bookDao.saveNewBook(book);

        assertThat(saved).isNotNull();
    }

    @Test
    void testGetBookByName() {
        Book book = bookDao.findBookByTitle("Clean Code");

        assertThat(book).isNotNull();
    }

    @Test
    void testGetBook() {
        Book book = bookDao.getById(3L);

        assertThat(book.getId()).isNotNull();
    }
}