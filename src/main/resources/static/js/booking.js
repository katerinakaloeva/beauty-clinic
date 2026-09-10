const bookingForm = document.getElementById("booking-form");
const treatmentSelect = document.getElementById("treatment-id");
const appointmentDateInput = document.getElementById("appointment-date");
const startTimeSelect = document.getElementById("start-time");
const submitButton = document.getElementById("submit-booking");
const formMessage = document.getElementById("form-message");

function createOption(value, text) {
    const option = document.createElement("option");
    option.value = value;
    option.textContent = text;

    return option;
}

function formatTime(time) {
    return time.substring(0, 5);
}

function showMessage(message, type) {
    formMessage.textContent = message;
    formMessage.className = `form-message is-${type}`;
    formMessage.hidden = false;
}

function clearMessage() {
    formMessage.textContent = "";
    formMessage.className = "form-message";
    formMessage.hidden = true;
}

function resetStartTimes(message) {
    startTimeSelect.replaceChildren(createOption("", message));
    startTimeSelect.disabled = true;
    submitButton.disabled = true;
}

function setMinimumAppointmentDate() {
    const today = new Date();
    const localDate = [
        today.getFullYear(),
        String(today.getMonth() + 1).padStart(2, "0"),
        String(today.getDate()).padStart(2, "0")
    ].join("-");

    appointmentDateInput.min = localDate;
}

async function getErrorMessage(response, fallbackMessage) {
    if (response.status === 403) {
        return "Η ενέργεια απορρίφθηκε για λόγους ασφαλείας. Κάνε ανανέωση και ξαναπροσπάθησε.";
    }

    try {
        const errorResponse = await response.json();
        return errorResponse.message ?? fallbackMessage;
    } catch {
        return fallbackMessage;
    }
}

async function loadTreatments() {
    try {
        const response = await fetch("/api/treatments");

        if (!response.ok) {
            throw new Error(
                await getErrorMessage(
                    response,
                    "Δεν ήταν δυνατή η φόρτωση των θεραπειών."
                )
            );
        }

        const treatments = await response.json();

        treatmentSelect.replaceChildren(
            createOption("", "Επίλεξε θεραπεία")
        );

        treatments.forEach(treatment => {
            treatmentSelect.appendChild(
                createOption(treatment.id, treatment.name)
            );
        });

        treatmentSelect.disabled = treatments.length === 0;

        if (treatments.length === 0) {
            showMessage("Δεν υπάρχουν διαθέσιμες θεραπείες.", "error");
        }
    } catch (error) {
        treatmentSelect.replaceChildren(
            createOption("", "Δεν φορτώθηκαν οι θεραπείες")
        );
        showMessage(error.message, "error");
    }
}

async function loadAvailableTimes() {
    const treatmentId = treatmentSelect.value;
    const appointmentDate = appointmentDateInput.value;

    resetStartTimes("Φόρτωση διαθέσιμων ωρών...");

    if (!treatmentId || !appointmentDate) {
        resetStartTimes("Επίλεξε θεραπεία και ημερομηνία");
        return;
    }

    try {
        const parameters = new URLSearchParams({
            treatmentId,
            appointmentDate
        });

        const response = await fetch(
            `/api/bookings/available-times?${parameters}`,
            {credentials: "same-origin"}
        );

        if (!response.ok) {
            throw new Error(
                await getErrorMessage(
                    response,
                    "Δεν ήταν δυνατή η φόρτωση των διαθέσιμων ωρών."
                )
            );
        }

        const availableTimes = await response.json();

        startTimeSelect.replaceChildren(
            createOption("", "Επίλεξε ώρα")
        );

        availableTimes.forEach(time => {
            startTimeSelect.appendChild(
                createOption(time, formatTime(time))
            );
        });

        startTimeSelect.disabled = availableTimes.length === 0;

        if (availableTimes.length === 0) {
            showMessage("Δεν υπάρχουν διαθέσιμες ώρες για αυτή την ημέρα.", "error");
        } else {
            clearMessage();
        }
    } catch (error) {
        resetStartTimes("Δεν φορτώθηκαν διαθέσιμες ώρες");
        showMessage(error.message, "error");
    }
}

function getCsrfToken() {
    const csrfCookie = document.cookie
        .split("; ")
        .find(cookie => cookie.startsWith("XSRF-TOKEN="));

    return csrfCookie?.substring("XSRF-TOKEN=".length);
}

async function createBooking(event) {
    event.preventDefault();
    clearMessage();

    const csrfToken = getCsrfToken();

    if (!csrfToken) {
        showMessage(
            "Δεν βρέθηκε token ασφαλείας. Κάνε ανανέωση ή ξανασυνδέσου.",
            "error"
        );
        return;
    }

    const booking = {
        treatmentId: Number(treatmentSelect.value),
        appointmentDate: appointmentDateInput.value,
        startTime: startTimeSelect.value
    };

    submitButton.disabled = true;
    submitButton.textContent = "Αποθήκευση...";

    try {
        const response = await fetch("/api/bookings", {
            method: "POST",
            credentials: "same-origin",
            headers: {
                "Content-Type": "application/json",
                "X-XSRF-TOKEN": csrfToken
            },
            body: JSON.stringify(booking)
        });

        if (!response.ok) {
            throw new Error(
                await getErrorMessage(
                    response,
                    "Η κράτηση δεν ολοκληρώθηκε."
                )
            );
        }

        const createdBooking = await response.json();

        bookingForm.reset();
        resetStartTimes("Επίλεξε θεραπεία και ημερομηνία");
        showMessage(
            `Το ραντεβού σου καταχωρίστηκε για ${createdBooking.appointmentDate} στις ${formatTime(createdBooking.startTime)}.`,
            "success"
        );
    } catch (error) {
        showMessage(error.message, "error");
    } finally {
        submitButton.textContent = "Επιβεβαίωση κράτησης";

        if (
            treatmentSelect.value
            && appointmentDateInput.value
            && startTimeSelect.value
        ) {
            submitButton.disabled = false;
        }
    }
}

setMinimumAppointmentDate();
resetStartTimes("Επίλεξε θεραπεία και ημερομηνία");
loadTreatments();

treatmentSelect.addEventListener("change", loadAvailableTimes);
appointmentDateInput.addEventListener("change", loadAvailableTimes);
startTimeSelect.addEventListener("change", () => {
    submitButton.disabled = !startTimeSelect.value;
});
bookingForm.addEventListener("submit", createBooking);
