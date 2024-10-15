package com.ask.jpa.vote;

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional


interface VoteRepository : JpaRepository<Vote, String> {
  @EntityGraph(attributePaths = ["voteItems"])
  fun findWithVoteItemsById(id: String): Vote?

  @Transactional
  fun removeByTitle(title: String): Int

  @Transactional
  fun deleteByTitle(title: String): Int
}

fun VoteRepository.getWithVoteItems(id: String) = findWithVoteItemsById(id) ?: throw IllegalStateException("vote not found")


