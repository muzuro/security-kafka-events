package com.mzr.sm.event

import org.springframework.security.core.Authentication

data class Event(val type: String, val value: String, val authentication: Authentication)
