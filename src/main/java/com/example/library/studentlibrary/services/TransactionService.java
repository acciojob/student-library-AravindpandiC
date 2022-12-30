package com.example.library.studentlibrary.services;

import com.example.library.studentlibrary.models.*;
import com.example.library.studentlibrary.repositories.BookRepository;
import com.example.library.studentlibrary.repositories.CardRepository;
import com.example.library.studentlibrary.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class TransactionService {

    @Autowired
    BookRepository bookRepository5;

    @Autowired
    CardRepository cardRepository5;

    @Autowired
    TransactionRepository transactionRepository5;

    @Value("${books.max_allowed}")
    int max_allowed_books;

    @Value("${books.max_allowed_days}")
    int getMax_allowed_days;

    @Value("${books.fine.per_day}")
    int fine_per_day;

    public String issueBook(int cardId, int bookId) throws Exception {
        //check whether bookId and cardId already exist
        //conditions required for successful transaction of issue book:
        //1. book is present and available
        // If it fails: throw new Exception("Book is either unavailable or not present");

        Book book = bookRepository5.findById(bookId).orElseThrow(()->new Exception("Book is either unavailable or not present"));
        if(book==null || !book.isAvailable()) throw new Exception("Book is either unavailable or not present");
        //2. card is present and activated
        // If it fails: throw new Exception("Card is invalid");

        Card card = cardRepository5.findById(cardId).orElseThrow(()->new Exception("Card is invalid"));
        if(card==null || card.getCardStatus().toString().equals("DEACTIVATED")) throw new Exception("Card is invalid");

        //3. number of books issued against the card is strictly less than max_allowed_books
        // If it fails: throw new Exception("Book limit has reached for this card");

        if(card.getBooks().size()>=max_allowed_books) throw new Exception("Book limit has reached for this card");

        //If the transaction is successful, save the transaction to the list of transactions and return the id
        book.setAvailable(false);
        bookRepository5.save(book);

        Transaction transaction = Transaction.builder().card(card).
                                  book(book).
                                  transactionStatus(TransactionStatus.SUCCESSFUL).
                                  isIssueOperation(true).build();
        transactionRepository5.save(transaction);
        //Note that the error message should match exactly in all cases

       return transaction.getTransactionId(); //return transactionId instead
    }

    public Transaction returnBook(int cardId, int bookId) throws Exception{

        List<Transaction> transactions = transactionRepository5.find(cardId, bookId,TransactionStatus.SUCCESSFUL, true);
        Transaction transaction = transactions.get(transactions.size() - 1);

        //for the given transaction calculate the fine amount considering the book has been returned exactly when this function is called
        Date transactionDate = transaction.getTransactionDate();
        Date currentDate = new Date();
        long timeDiff = currentDate.getTime()-transactionDate.getTime();
        long dayDiff
                = (timeDiff
                / (1000 * 60 * 60 * 24))
                % 365;
        int totalFineAmount = fine_per_day * (int) dayDiff;
        transaction.getBook().setAvailable(true);
        bookRepository5.save(transaction.getBook());
        //make the book available for other users

        Transaction returnBookTransaction = Transaction.builder().transactionStatus(TransactionStatus.SUCCESSFUL).
                                       card(transaction.getCard()).book(transaction.getBook()).
                                        fineAmount(totalFineAmount).isIssueOperation(true).build();
        transactionRepository5.save(returnBookTransaction);
        //make a new transaction for return book which contains the fine amount as well

        return returnBookTransaction; //return the transaction after updating all details
    }
}