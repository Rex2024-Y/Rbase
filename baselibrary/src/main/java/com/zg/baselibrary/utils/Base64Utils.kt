package com.zg.baselibrary.utils

import kotlin.math.min

object Base64Utils {
    val encoder: Encoder
        get() = Encoder.RFC4648

    class Encoder private constructor(
        private val isURL: Boolean,
        private val newline: ByteArray?,
        private val linemax: Int,
        private val doPadding: Boolean
    ) {

        private fun encodedOutLength(srclen: Int, throwOOME: Boolean): Int {
            var len = 0
            try {
                len = if (doPadding) {
                    Math.multiplyExact(4, Math.addExact(srclen, 2) / 3)
                } else {
                    val n = srclen % 3
                    Math.addExact(Math.multiplyExact(4, srclen / 3), if (n == 0) 0 else n + 1)
                }
                if (linemax > 0) {                             // line separators
                    len = Math.addExact(len, (len - 1) / linemax * newline!!.size)
                }
            } catch (ex: ArithmeticException) {
                len = if (throwOOME) {
                    throw OutOfMemoryError("Encoded size is too large")
                } else {
                    // let the caller know that encoded bytes length
                    // is too large
                    -1
                }
            }
            return len
        }

        fun encode(src: ByteArray): ByteArray {
            val len = encodedOutLength(src.size, true) // dst array size
            val dst = ByteArray(len)
            val ret = encode0(src, 0, src.size, dst)
            return if (ret != dst.size) dst.copyOf(ret) else dst
        }

        @Suppress("deprecation")
        fun encodeToString(src: ByteArray): String {
            val encoded = encode(src)
            return String(encoded, 0, encoded.size)
        }

        private fun encodeBlock(
            src: ByteArray,
            sp: Int,
            sl: Int,
            dst: ByteArray,
            dp: Int,
            isURL: Boolean
        ) {
            val base64 = if (isURL) toBase64URL else toBase64
            var sp0 = sp
            var dp0 = dp
            while (sp0 < sl) {
                val bits = src[sp0++].toInt() and 0xff shl 16 or (
                        src[sp0++].toInt() and 0xff shl 8) or
                        (src[sp0++].toInt() and 0xff)
                dst[dp0++] = base64[(bits ushr 18) and 0x3f].code.toByte()
                dst[dp0++] = base64[(bits ushr 12) and 0x3f].code.toByte()
                dst[dp0++] = base64[(bits ushr 6) and 0x3f].code.toByte()
                dst[dp0++] = base64[bits and 0x3f].code.toByte()
            }
        }

        private fun encode0(src: ByteArray, off: Int, end: Int, dst: ByteArray): Int {
            val base64 = if (isURL) toBase64URL else toBase64
            var sp = off
            var slen = (end - off) / 3 * 3
            val sl = off + slen
            if (linemax > 0 && slen > linemax / 4 * 3) slen = linemax / 4 * 3
            var dp = 0
            while (sp < sl) {
                val sl0 = min((sp + slen).toDouble(), sl.toDouble()).toInt()
                encodeBlock(src, sp, sl0, dst, dp, isURL)
                val dlen = (sl0 - sp) / 3 * 4
                dp += dlen
                sp = sl0
                if (dlen == linemax && sp < end) {
                    for (b in newline!!) {
                        dst[dp++] = b
                    }
                }
            }
            if (sp < end) {               // 1 or 2 leftover bytes
                val b0 = src[sp++].toInt() and 0xff
                dst[dp++] = base64[b0 shr 2].code.toByte()
                if (sp == end) {
                    dst[dp++] = base64[(b0 shl 4) and 0x3f].code.toByte()
                    if (doPadding) {
                        dst[dp++] = '='.code.toByte()
                        dst[dp++] = '='.code.toByte()
                    }
                } else {
                    val b1 = src[sp++].toInt() and 0xff
                    dst[dp++] = base64[((b0 shl 4) and 0x3f) or (b1 shr 4)].code.toByte()
                    dst[dp++] = base64[(b1 shl 2) and 0x3f].code.toByte()
                    if (doPadding) {
                        dst[dp++] = '='.code.toByte()
                    }
                }
            }
            return dp
        }

        companion object {

            private val toBase64 = charArrayOf(
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
                'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
            )

            private val toBase64URL = charArrayOf(
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
                'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_'
            )
            val RFC4648 = Encoder(false, null, -1, true)
        }
    }
}
