package com.castillo.fit

import org.junit.Assert.*
import org.junit.Test

class BookingRulesTest {
    @Test fun repeatedActiveBookingIsRejected() {
        val item = catalog.first()
        val records = listOf(Booking(1,item.id,item.name,"Hoy","9:00 am"))
        assertTrue(isDuplicate(records,item,"Hoy","9:00 am"))
        assertFalse(isDuplicate(records,item,"Mañana","9:00 am"))
    }
    @Test fun cancelledBookingAllowsSameSlotAgain() {
        val item = catalog.first()
        val records = listOf(Booking(1,item.id,item.name,"Hoy","9:00 am","Cancelada"))
        assertFalse(isDuplicate(records,item,"Hoy","9:00 am"))
        assertEquals(item.capacity-4,availableSeats(records,item,"Hoy","9:00 am"))
    }
    @Test fun capacityNeverBecomesNegative() {
        val item = catalog.first()
        val records = (1..30).map { Booking(it,item.id,item.name,"Hoy","9:00 am") }
        assertEquals(0,availableSeats(records,item,"Hoy","9:00 am"))
    }
    @Test fun otherSlotsDoNotReduceCapacity() {
        val item = catalog.first()
        val records = listOf(Booking(1,item.id,item.name,"Mañana","9:00 am"))
        assertEquals(item.capacity-4,availableSeats(records,item,"Hoy","9:00 am"))
    }
}

