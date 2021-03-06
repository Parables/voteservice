package io.appwish.voteservice.verticle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.concurrent.Future;

import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.appwish.voteservice.TestData;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import oi.appwish.voteservice.eventbus.Address;
import oi.appwish.voteservice.eventbus.EventBusConfigurer;
import oi.appwish.voteservice.repository.VoteRepository;
import oi.appwish.voteservice.verticle.DatabaseVerticle;

@ExtendWith(VertxExtension.class)
class DatabaseVerticleTest {

  @Test
  void should_expose_database_service(final Vertx vertx, final VertxTestContext context) {
    // given
    final VoteRepository repository = mock(VoteRepository.class);
    final DatabaseVerticle verticle = new DatabaseVerticle(repository);
    final EventBusConfigurer util = new EventBusConfigurer(vertx.eventBus());
    when(repository.findAll(TestData.ALL_VOTE_QUERY))
      .thenReturn(Future.succeededFuture(TestData.VOTES));

    util.registerCodecs();

    // when
    vertx.deployVerticle(verticle, new DeploymentOptions(), context.succeeding());

    vertx.eventBus().request(Address.FIND_ALL_VOTES.get(), TestData.ALL_VOTE_QUERY, event -> {
      // then
      context.verify(() -> {
        assertTrue(event.succeeded());
        assertEquals(TestData.VOTES, event.result().body());
        context.completeNow();
      });

    });
  }
}
