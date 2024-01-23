package com.actively.datasource

import app.cash.sqldelight.Transacter
import app.cash.sqldelight.TransactionWithReturn
import app.cash.sqldelight.TransactionWithoutReturn
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

suspend fun <R> Transacter.suspendingTransactionWithResult(
    context: CoroutineContext,
    body: TransactionWithReturn<R>.() -> R
): R = withContext(context) {
    transactionWithResult(bodyWithReturn = body)
}

suspend fun Transacter.suspendingTransaction(
    context: CoroutineContext,
    body: TransactionWithoutReturn.() -> Unit
) = withContext(context) {
    transaction(body = body)
}
