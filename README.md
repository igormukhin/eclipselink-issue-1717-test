# EclipseLink Issue 1717 Demo

Abstract: Using the function "COALESCE" in a highly concurrent environment results in errors.

Example stack trace:
```
Exception [EclipseLink-6168] (Eclipse Persistence Services - 2.7.14.v20231208-d05cebc9b0): org.eclipse.persistence.exceptions.QueryException
Exception Description: Query failed to prepare, unexpected error occurred: [java.lang.NullPointerException: Cannot read the array length because "this.argumentIndices" is null].
Internal Exception: java.lang.NullPointerException: Cannot read the array length because "this.argumentIndices" is null
Query: ReadAllQuery(referenceClass=Person jpql="SELECT p FROM Person p WHERE p.name = coalesce(p.name, 'x') AND p.name = 'cacheBuster.Thread-15.76'")
	at org.eclipse.persistence.exceptions.QueryException.prepareFailed(QueryException.java:1598)
	at org.eclipse.persistence.queries.DatabaseQuery.checkPrepare(DatabaseQuery.java:694)
	at org.eclipse.persistence.queries.ObjectLevelReadQuery.checkPrepare(ObjectLevelReadQuery.java:968)
	at org.eclipse.persistence.queries.DatabaseQuery.checkPrepare(DatabaseQuery.java:624)
	at org.eclipse.persistence.internal.jpa.EJBQueryImpl.buildEJBQLDatabaseQuery(EJBQueryImpl.java:194)
	at org.eclipse.persistence.internal.jpa.EJBQueryImpl.buildEJBQLDatabaseQuery(EJBQueryImpl.java:118)
	at org.eclipse.persistence.internal.jpa.EJBQueryImpl.<init>(EJBQueryImpl.java:104)
	at org.eclipse.persistence.internal.jpa.EJBQueryImpl.<init>(EJBQueryImpl.java:88)
	at org.eclipse.persistence.internal.jpa.EntityManagerImpl.createQuery(EntityManagerImpl.java:1749)
	at org.eclipse.persistence.internal.jpa.EntityManagerImpl.createQuery(EntityManagerImpl.java:1772)
	at org.example.Launcher.executeQueries(Launcher.java:65)
	at org.example.Launcher.lambda$run$0(Launcher.java:35)
	at java.base/java.lang.Thread.run(Thread.java:1570)
Caused by: java.lang.NullPointerException: Cannot read the array length because "this.argumentIndices" is null
	at org.eclipse.persistence.expressions.ExpressionOperator.printCollection(ExpressionOperator.java:2391)
	at org.eclipse.persistence.internal.expressions.ArgumentListFunctionExpression.printSQL(ArgumentListFunctionExpression.java:102)
	at org.eclipse.persistence.expressions.ExpressionOperator.printDuo(ExpressionOperator.java:2447)
	at org.eclipse.persistence.internal.expressions.CompoundExpression.printSQL(CompoundExpression.java:291)
	at org.eclipse.persistence.internal.expressions.RelationExpression.printSQL(RelationExpression.java:908)
	at org.eclipse.persistence.expressions.ExpressionOperator.printDuo(ExpressionOperator.java:2442)
	at org.eclipse.persistence.internal.expressions.CompoundExpression.printSQL(CompoundExpression.java:291)
	at org.eclipse.persistence.internal.expressions.ExpressionSQLPrinter.translateExpression(ExpressionSQLPrinter.java:337)
	at org.eclipse.persistence.internal.expressions.ExpressionSQLPrinter.printExpression(ExpressionSQLPrinter.java:135)
	at org.eclipse.persistence.internal.expressions.SQLSelectStatement.printSQLWhereClause(SQLSelectStatement.java:1805)
	at org.eclipse.persistence.internal.expressions.SQLSelectStatement.printSQL(SQLSelectStatement.java:1754)
	at org.eclipse.persistence.internal.databaseaccess.DatabasePlatform.printSQLSelectStatement(DatabasePlatform.java:3593)
	at org.eclipse.persistence.platform.database.H2Platform.printSQLSelectStatement(H2Platform.java:56)
	at org.eclipse.persistence.internal.expressions.SQLSelectStatement.buildCall(SQLSelectStatement.java:868)
	at org.eclipse.persistence.internal.expressions.SQLSelectStatement.buildCall(SQLSelectStatement.java:879)
	at org.eclipse.persistence.descriptors.ClassDescriptor.buildCallFromStatement(ClassDescriptor.java:885)
	at org.eclipse.persistence.internal.queries.StatementQueryMechanism.setCallFromStatement(StatementQueryMechanism.java:407)
	at org.eclipse.persistence.internal.queries.StatementQueryMechanism.prepareSelectAllRows(StatementQueryMechanism.java:332)
	at org.eclipse.persistence.internal.queries.ExpressionQueryMechanism.prepareSelectAllRows(ExpressionQueryMechanism.java:1724)
	at org.eclipse.persistence.queries.ReadAllQuery.prepareSelectAllRows(ReadAllQuery.java:910)
	at org.eclipse.persistence.queries.ReadAllQuery.prepare(ReadAllQuery.java:841)
	at org.eclipse.persistence.queries.DatabaseQuery.checkPrepare(DatabaseQuery.java:675)
	... 11 more
```

## How to run

```shell
./gradlew run
```