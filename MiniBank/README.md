# MiniBank Banking System 🏦
> **CEUE203: Object Oriented Programming — Semester 3 Capstone Project**  
> **Student:** Ruchir Agrawal (`25IT001`) | **Department:** Information Technology, CSPIT, CHARUSAT  
> **Faculty:** Sagar Patel

---

## 📌 Project Overview
**MiniBank** is a full-featured, console-based banking application built in Java 17+. The application consolidates the key object-oriented paradigms, concurrency architectures, reflection frameworks, and modern file I/O studied across the entire 14-week semester curriculum into an enterprise-grade banking system.

---

## 🏗️ Architectural Packages
The project adheres strictly to layered clean-architecture principles:

```
minibank/
├── app/
│   └── MiniBank.java                    # Terminal CLI, MenuOption dispatch, switch expressions
├── model/
│   ├── BankInfo.java                    # Modern Java record for immutable branch metadata
│   ├── MenuOption.java                  # Enum encapsulating console menu choices
│   ├── Transactable.java                # Interface for deposit/withdraw contracts
│   ├── InterestBearing.java             # Interface with default method for yearly interest
│   ├── WithdrawRule.java                # Functional interface (@FunctionalInterface) for lambdas
│   ├── Premium.java                     # Marker interface for high-tier accounts
│   ├── Customer.java                    # Customer entity with static nested Address & Cloneable
│   ├── Account.java                     # Abstract base entity with synchronized methods & @Id
│   ├── SavingsAccount.java              # 4.0% interest account with minimum balance guard
│   ├── CurrentAccount.java              # 0.0% interest commercial account with overdraft limit
│   └── FixedDepositAccount.java         # 7.0% interest investment account with locked withdrawal
├── model/annotation/
│   ├── Id.java                          # Runtime marker annotation for primary keys
│   ├── Positive.java                    # Runtime constraint annotation for numeric values
│   ├── MaxLength.java                   # Runtime constraint annotation for String bounds
│   └── Audit.java                       # Method marker for reflective execution tracing
├── exception/
│   ├── BankException.java               # Base checked banking exception
│   ├── InsufficientFundsException.java  # Custom exception carrying shortfall details
│   ├── AccountNotFoundException.java    # Account lookup failure exception
│   └── InvalidAmountException.java      # Guard against non-positive transaction amounts
├── repository/
│   ├── Repository.java                  # Generic Repository<T> using reflection for @Id
│   └── AccountStore.java                # HashMap store with Comparator sorting
├── concurrency/
│   ├── AccountWorker.java               # Runnable worker simulating high-frequency transactions
│   ├── TransactionProcessor.java        # Fixed thread pool executor & ConcurrentHashMap
│   └── TransactionQueue.java            # Producer-Consumer queue with wait() and notifyAll()
├── util/
│   ├── AnnotationValidator.java         # Reflection validator for @Positive and @MaxLength
│   ├── StatePersister.java              # Object serialization/deserialization engine
│   ├── TransactionLog.java              # Append-only NIO audit transaction log
│   ├── ReportGenerator.java             # NIO.2 directory stream walker and daily report synthesizer
│   └── TransactionAuditSession.java     # AutoCloseable session for try-with-resources
├── service/
│   └── BankService.java                 # Facade coordinating repository, rules, and concurrency
└── test/
    └── MiniBankTest.java                # Automated JUnit 4 unit & thread-safety test suite
```

---

## ⚡ Core Features Implemented

1. **Modern Core Java (Practicals 1–3):**
   - Java 16+ `record BankInfo` for immutable metadata.
   - `enum MenuOption` with modern Java 14+ switch expressions.
   - Data encapsulation, static counters for account numbers (`AC1001`, `AC1002`).
   - Standard object contract (`equals`, `hashCode`, `toString`) and nested `Customer.Address` with deep cloning (`Cloneable`).

2. **Polymorphic Account Hierarchy (Practicals 5–6):**
   - `abstract class Account` extended by `SavingsAccount`, `CurrentAccount`, and `FixedDepositAccount`.
   - Polymorphic method dispatch for `interestRate()` and `canWithdraw()`.
   - Interface evolution with `default` methods (`yearlyInterest()`) in `InterestBearing`.
   - Functional interface `@FunctionalInterface WithdrawRule` used with lambda expressions.

3. **Custom Annotations & Java Reflection (Practical 7):**
   - Declarative metadata: `@Id`, `@Positive`, `@MaxLength`, and `@Audit`.
   - Reflection engine in `AnnotationValidator` inspecting runtime fields and enforcing validation rules.

4. **Robust Exception Hierarchy (Practical 8):**
   - Checked exception hierarchy rooted at `BankException`.
   - `InsufficientFundsException` carries exact shortfall values.
   - Try-with-resources audit sessions using `AutoCloseable`.

5. **Concurrency & Thread Safety (Practicals 9–10):**
   - Thread-safe synchronized operations preventing race conditions under heavy load.
   - Deadlock-free funds transfer by deterministically locking accounts in natural key order (`compareTo`).
   - Managed `ExecutorService` thread pool in `TransactionProcessor`.
   - Inter-thread coordination using `wait()` and `notifyAll()` in `TransactionQueue`.

6. **Persistence & NIO File I/O (Practical 11):**
   - Object serialization (`ObjectOutputStream` / `ObjectInputStream`) saving state to `data/accounts.ser`.
   - High-performance append-only logging with `java.nio.file.Files`.
   - End-of-day summary reports compiled by walking directories with `DirectoryStream`.

7. **Generics & Collections Framework (Practicals 12–14):**
   - In-memory storage with `HashMap` and sorted projections using `Comparator`.
   - Reusable `Repository<T>` with reflection-based key resolution and bounded type parameters (`<N extends Number>`).
   - Comprehensive automated unit tests using JUnit 4.

---

## 🚀 How to Run

### Method 1: Runnable JAR (Pre-packaged)
```bash
java -jar minibank.jar
```
*(Or simply double-click `run.bat` on Windows)*

### Method 2: Compile & Run from Source
```bash
# Compile all source files
javac -d bin -cp "..\lib\*" src/minibank/**/*.java

# Run main application
java -cp "bin;..\lib\*" minibank.app.MiniBank
```

### Method 3: Run Automated JUnit 4 Tests
```bash
java -cp "bin;..\lib\*" org.junit.runner.JUnitCore minibank.test.MiniBankTest
```
All tests verify deposit arithmetic, negative guards, overdraft rules, shortfall calculations, and concurrent thread safety.
