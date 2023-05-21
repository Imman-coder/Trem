package com.example.myapplication.network.util

import kotlinx.collections.immutable.PersistentCollection
import kotlinx.collections.immutable.persistentListOf


fun <T, E> PersistentCollection<T>.mapToPersistentCollection(transform: (T) -> E): PersistentCollection<E> {
    var col : PersistentCollection<E> = persistentListOf();
    this.forEach {col = col.add(transform(it)) }
    return col;
}
fun <T, E> List<T>.mapToPersistentCollection(transform: (T) -> E): PersistentCollection<E> {
    var col : PersistentCollection<E> = persistentListOf();
    this.forEach {col = col.add(transform(it)) }
    return col;
}