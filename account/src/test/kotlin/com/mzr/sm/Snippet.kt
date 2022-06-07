package com.mzr.sm

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.apache.commons.lang3.time.DateUtils
import java.util.Date

fun main(args: Array<String>) {
    val jwtBuilder = JWT.create()
    val claims: MutableMap<String, Any> = HashMap()
    claims["authorities"] = listOf("SYSTEM")
    claims["create_time"] = Date()
    claims["roles"] = "SYSTEM"
    claims["token_author"] = "Tudor Prodan"
    claims["token_purpose"] = "Qvin Service Account"
    claims["userId"] = "00000000-0000-0000-0000-000000000000"
    claims["user_name"] = "00000000-0000-0000-0000-000000000000"

    jwtBuilder.withClaim("authorities", listOf("SYSTEM"))
    jwtBuilder.withClaim("create_time", Date())
    jwtBuilder.withClaim("roles", "SYSTEM")
    jwtBuilder.withClaim("token_author", "Tudor Prodan")
    jwtBuilder.withClaim("token_purpose", "Qvin Service Account")
    jwtBuilder.withClaim("userId", "00000000-0000-0000-0000-000000000000")
    jwtBuilder.withClaim("user_name", "00000000-0000-0000-0000-000000000000")

    print(jwtBuilder
        .withNotBefore(Date())
        .withExpiresAt(DateUtils.addDays(Date(), 1))
        .sign(Algorithm.HMAC256("secret")))
}