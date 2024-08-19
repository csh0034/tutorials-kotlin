package com.ask.jpa.vote;

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface VoteRepository : JpaRepository<Vote, String> {
  @EntityGraph(attributePaths = ["voteItems"])
  fun findWithVoteItemsById(id: String): Vote?
}

fun VoteRepository.getWithVoteItems(id: String) = findWithVoteItemsById(id) ?: throw IllegalStateException("vote not found")


