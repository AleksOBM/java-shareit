package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

	@Query("""
		select b
		from Booking b
		left join Item i on i.id = b.item.id
		where i.owner.id = :ownerId
		order by b.start desc
	""")
	List<Booking> findAllByOwner(Long ownerId);

	@Query("""
		select b
		from Booking b
		left join Item i on i.id = b.item.id
		where i.owner.id = :ownerId
		  and b.start <= CURRENT_TIMESTAMP
		  and b.end >= CURRENT_TIMESTAMP
		order by b.start desc
	""")
	List<Booking> findCurrentByOwner(Long ownerId);

	@Query("""
		select b
		from Booking b
		left join Item i on i.id = b.item.id
		where i.owner.id = :ownerId
		  and b.end < CURRENT_TIMESTAMP
		order by b.start desc
	""")
	List<Booking> findPastByOwner(Long ownerId);

	@Query("""
		select b
		from Booking b
		left join Item i on i.id = b.item.id
		where i.owner.id = :ownerId
		  and b.start > CURRENT_TIMESTAMP
		order by b.start desc
	""")
	List<Booking> findFutureByOwner(Long ownerId);

	@Query("""
		select b
		from Booking b
		left join Item i on i.id = b.item.id
		where i.owner.id = :ownerId
			and b.status = :status
		order by b.start desc
	""")
	List<Booking> findAllByOwnerAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

	@Query("""
	    select b
	    from Booking b
	    where b.booker.id = :bookerId
	      and b.start <= CURRENT_TIMESTAMP
	      and b.end >= CURRENT_TIMESTAMP
	    order by b.start desc
	""")
	List<Booking> findCurrentByBooker(Long bookerId);

	@Query("""
	    select b
	    from Booking b
	    where b.booker.id = :bookerId
	      and b.end < CURRENT_TIMESTAMP
	    order by b.start desc
	""")
	List<Booking> findPastByBooker(Long bookerId);

	@Query("""
	    select b
	    from Booking b
	    where b.booker.id = :bookerId
	      and b.start > CURRENT_TIMESTAMP
	    order by b.start desc
	""")
	List<Booking> findFutureByBooker(Long bookerId);

	List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

	List<Booking> findAllByBooker_IdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

	List<Booking> findBookingByItem_Id(Long itemId);
}
