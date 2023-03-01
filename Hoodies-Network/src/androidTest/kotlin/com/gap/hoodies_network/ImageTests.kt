package com.gap.hoodies_network

import android.graphics.Bitmap
import android.util.Base64
import android.widget.ImageView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.gap.hoodies_network.core.Failure
import com.gap.hoodies_network.core.HoodiesNetworkClient
import com.gap.hoodies_network.core.Success
import com.gap.hoodies_network.mockwebserver.ServerManager
import com.gap.hoodies_network.testObjects.testInterceptor
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayOutputStream

@RunWith(AndroidJUnit4::class)
class ImageTests {
    private val interceptor = testInterceptor(InstrumentationRegistry.getInstrumentation().context)

    @Before
    fun startMockWebServer() {
        ServerManager.setup(interceptor.context)
    }

    @After
    fun stopServer() {
        ServerManager.stop()
    }

    @Test
    fun getImageTestMOCKWEBSERVER() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build()

            when (val result = client.getImage(
                "image",
                null,
                0,
                0,
                ImageView.ScaleType.CENTER,
                Bitmap.Config.ALPHA_8
            )) {
                is Success -> {
                    //Convert image to ByteArray
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    result.value!!.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                    val byteArray: ByteArray = byteArrayOutputStream.toByteArray()

                    //Convert the byteArray to base64
                    val encoded: String = Base64.encodeToString(byteArray, Base64.DEFAULT).filter { !it.isWhitespace() }
                    val base64 ="iVBORw0KGgoAAAANSUhEUgAAAIAAAACACAIAAABMXPacAAAAAXNSR0IArs4c6QAAAANzQklUCAgI"+
                                "2+FP4AAAIABJREFUeJztfdmTncd13zmnu7/1bnNnAbESGwlSFCnTsiRLpGUpFsOoYjmuSspxKovj"+
                                "xKpUpRJX/oG85impykMsJ3Hsh3hJpVxWbLnklZRly5IlaiFpkiBIgCD2bba7f1v3OXn47p25s9zB"+
                                "zGBIgIB+hSKBu/TX9/y6T5+tuxHuJyAAAMjqv3xtpqeaqOjGrVuOnUz+6t2Cutsd2GsgIJY8QOgH"+
                                "szMzRhulFSmVJMnd7dqm0He7A3sDXBn3AgCCAHEUT09NKdQiTkSMd48OtfuEABjnAKAahVNTU4go"+
                                "IgAgIt1u9x7UP3DfELAiXASohNFMc0YEQQBRLPPi8nL/ntQ/cN8QsALPeNNTTWQUBEQsXLG0tDTI"+
                                "0rvdr4m4DwggAABgAFBIzWaTSLMIABTWLiwvpVl6byqfEnS3O7BnQIBGoxGYoPynALRarXtc+nBf"+
                                "EMAAjABRGFWjKoiICCC2up1e2tso/RUj9R7BfUAAAAACTtXrpWgRMU3Tbre9ujKPCb20i+4d3A8E"+
                                "IMBUve5pvxS05WKxteiEVz5wrwl9HPcDAVrpSlyDUtCIrU6nsMXd7tR28YEnAAEqlQoRlcM8L9Je"+
                                "bxPVP/wo/nAN2GsoUpUoAhEAQJROp8NQ/n2DoBHh3lNHH2wCEKAax0YNvZk0zwZpWr6xiaBZ4N4S"+
                                "PsB94IhFUSQipXLp9HrDtVcARsFpACAkpRQR4QjMLCLMbK0FgJKZu8LOXSdg1Y9dFRgi8ERprITY"+
                                "ECDwA6P98vWiKFYCzgSotfaNCfxIa600lgSICAECgAABgHPOOWddnqZpliWZvQsJg7tOwGZYqz0Q"+
                                "17wwrluqcYyIIIKIg8GAhT1tgiCIw1BrbZQGIEF2UDBaC06UALAgABAKkSIt5EkQh5G1Nivy3mCQ"+
                                "JAnDug7ge7dy3DWTYO2vGpsHE19ZD4V0cP9+Qg0AINLv90kp3/eVUmOMcQF5jkmhi9xkbNgRiAAi"+
                                "KAsm93ThBRz6EAAAogKANMvavXaapiNP4vY9uRPcZZtsRMPmBCCKiEzqYuQHs7OzIASlhYkIAAyF"+
                                "Va6ANIOclXOa49loan+teagxc3imvq8eVn0idIUbtJOlq61bF+YXLiwtXV5WufHEDzjUYgBgkKat"+
                                "VisvcrmfCFibst3qdSwJANFaG6VKDa6UItSISAoQUZExeqhCSQAAGDnFQd/r5rW8cbJy+EcOn/ix"+
                                "E/G+OK776JEVJ8iMDCW9Qgo1Oco62eL5pddfeOPidy57N4LY1Q14CMo51+l0Ov2OwHtout5DBAAA"+
                                "AiqljDG+H/ralHLXWq9+RggABBmQgUd2PYnFIoNBGiTxwfCRT554/NnHKkcDV+e+7g9oIMgOnTCW"+
                                "Ew4Rh4QRg5CHJrJBJan1z2Xf//2X3/qrc7prYom18wlUb9BdXF5m4fHI0h4uCXd5DRiGzwA9zwu9"+
                                "MAgCY4xSq/nb8iOrlk/5w5EBAFkQkRGsyjO/X/tQ/Pg/ePTQxw7ous5VlknulGUUR3bjc9d1RrHW"+
                                "rEKJ/dy7+fri1//XX3VP9+rpjM8hIqZZNr80Xzhb9nVk4xIACNzpOnE31wAEMNqEYRiGYeB5CAoB"+
                                "yDkFVrNTYtE5kILYKRFCRygk4ARFqUKHAxXl5AFAjomr5b/4n/45P53fCK7lmDti4h34mGMEU83V"+
                                "ohuVv/7tvzn9R29VBlWfYy0mK9L5xcV8LMS0gYBdLhLvGwGrYwQBCCkMwzgMfS8s/aNyKVZilc2C"+
                                "oh/bfkVgyof9VZirmpnYm6uHka+roY/Gd37zhTPXfu/0cjeaQWBHNpX+oU8/9Nn/+MzS1HyKxVBH"+
                                "wVBlDX9q+ZQNr6+g1E6adcB+LWu8+Udnvvbr32osNipFVUjlRXFr8Va+12G+99sPUEhhGNYqFd8L"+
                                "ERhZNOeGM20ztGlIbjYyJw/VT87uOzFXn4uppl1F2UByjwtwmca0QFxWOXXnaTToFFOE8VsvnYu/"+
                                "4n/05z/CppXpDISGsh7htop7GM0m29F5TsVjX3ik3pz6k//ypzLPvos8E8xOT9+cn59U4LXOX9km"+
                                "3ksCcNWnRWAEjKJqLa74vg8AIkiCxqZh3p12ybEqfOSR8CNH9z3U8GseGmLFy+hyLUAFE1hiRhRk"+
                                "cMrrs7p4KyFVQwEl4IRR6Tivfv+3X9u/78Dcc9M5zAusH+blAN907K/AgSAiMRVoF/Ty3LMzn/ee"+
                                "//3//FW4Bl7h+yaYmZq5tXhrva84bH836/N7Wa60ul5B4AczzWa9WveMUs4GLovyXiVbPKi6zxxr"+
                                "/NwnT/zMjx7+xOHwaJDOQKfiWoHr+dz3ITeSarFKHAEjMAKnKriah3/+6vUlDIRUXQZVtM46UoYK"+
                                "OnP6zOHHjjT3NQqxTOv18u1j0Vh6ySggVrkcstkDMwcPH3jze29DBgjka18pStJ07Zd2r8nf83ox"+
                                "TWqqVp9qNIwxzKLEBUW/li5+yM/+4ZP1X/jk0edOVI7HSV1aoRtoyRAcgpShe+HhWgcAw+UKOdOV"+
                                "813vG2/fbOuKtumz++j5p0+cPX8pFyQKipTffuWtUydOxQ/FKaUjswlg6Oji1jE3XGMTgyBkmM7u"+
                                "n51qNF5//bSXe9p5vuc557Iix7HP75qB94qAsmdREM5OT8dhpIQ9Z6OiV0sWjwfpz3702M9/8uEf"+
                                "PxTsp3bVLQWu70GqxIG4oa8gUoYtV1sUAARBybz667fgW2cXBn7V2P5PPgTPP31Ck1y8eBXIAwqL"+
                                "rj392huHHz3SnGs4cYwCpUeNNGpocwx11JqfIVZbBnfwyCG29uLrlwIOUCDwgizLLLs7F9TeETA2"+
                                "eBCAAJq1RrMxpZVBZi0uTpePu/4/eqr+r549/uMHcAa6oe1pyUdr6dDeH7awSTql/K8kqvrydffd"+
                                "i8tpUDVF77lj0ZMz+tSsz2n77M1uoSsa/LznTr90+uDM4dnD04XJGWQYA916+G+mSVDQIVuTHzt+"+
                                "bP7CrYUriwYCA742qjfo37k/trcEIAKWReGzzZlKpULsfFdEeXcqXXzmSPzFv/PYTxyt7IOl2LY8"+
                                "SZUUuFPzGSVRtW9eyk7Pp6mJAtf5/MnqqZqErnP8yP7lQXHtxgJhoCDgBF//wRtO3PGjx33tszgA"+
                                "FNzqcesIWB0NgMxSpfijRz729nfP2rZTYrTRjjnL8zu05PfOCpKhaVCut0ZpZvCZo2T5mJf9/E8c"+
                                "+NTxRgxtzw6MFLhZ/qN0i7H8QRNsCRRyolr9xKICAC1QCTwDOUs2ze0vfvJQRZKvnF2wNMcYUcf8"+
                                "7a+fufGD+c/+y2ebT8y0glYCLOIYQW0mtqEvNrJk3NBgIk/8uqupBf3OyxckASxfZqxX6kmSDD3k"+
                                "3WIPCBjPkFTiSrPRUEjKFaHL4mTpY4cq/+RTTzwedav2JkKOwMIIOHnmlcHisdbXkcGC7bRw5AGA"+
                                "Z1Tsa3R9BQWxm0P7z545RfH8H75yveNVPS8O1XTru73fuvC7pz534uM/82NzB2u5LjJILRZCTpDL"+
                                "mAbA0GZVoNABsVZMvgoUa8ywe7Xz8kt/+/oLb2TvuFpR9zAqB49S6s5T/HtAwIr0G9Vao9EARhEI"+
                                "iuRA0vrZp6f/3o8cauJS4HqEDrjMneCmjvsozrM+/7L2U8QCnbRwKgAgheJ7GoQRAJCNFFNu8V88"+
                                "PXW0Iv/72zeupoX49RhrakG9+38vXX7x2mPPPPrEZ05NnWy6OM+DLKe8gKJM0SCQQuU5XzttUp+6"+
                                "1L7cufr6xXe+d2Hh3UXbcj6HTQiIDQkhIpB0ut3cFncYm9slAeNPRUQUaNRqjVpdWDwu/Lx7CHpf"+
                                "fO7kM0eianEjkMHq127b9O1+DCMkuXOomVmTMnpIJwojWJ/7Pmc/9chUc3bud/769BsLNxNTL0yY"+
                                "U5heL978f2+98hevxkfCfY/tO/TYgXg2Ciqe8jQA2NwO0ry/NN+61rlx7sbShaViwWFbRXlckaYW"+
                                "QwI8qmopkzaDZAAbFNdOsUsCBARolLkVadTq9XpdWBRDkLae9JJf+typp2YwKm4ZyMe+tu0ulh8k"+
                                "FOYhazJ8LgOm1rrhi84oRGERV04sQhIsKnb5Y1P22N8/9ecvX/2z11uX8p4EU+IZlCm/lbmOu/S3"+
                                "1y+oq8pH5SkiAgBm5oJtLuSAQBmoBKwMeASKRAsAIwhAlie9Xq876MPaRWzXk+AOVNC45qnVhcV3"+
                                "RZguP16x//65Dz9eTYOiZSTZ2vXfrFURMgX5DoOUNAGHrudxgWiBBYmstQ5QgBDA10opBMsrU0uQ"+
                                "QTQjOQaP8FOPH/3QqfCb5659662Lt7qQkQ8mYvIsRA4pHwyj/DLSjAqJWDQwglXC4BJhy8pkJs5R"+
                                "Icmm0r8T3JEZWq66U/UmgCiGSrb08Ur2y89/6NFqFtplLRZ4g2szGTL0LCWnoEPTf/HW8m//zZXr"+
                                "qTq2bzqAHJERAQCXpfrVt9rLEiHwnEk//9hUnbvDoitkAeXQW6bm777W+oNvXwDmJ47OffSA+vTR"+
                                "2skaxEWXuylnA7F5udwLEikiIgWi2Hk284pBlHUatj9tk2NB9vRDkUq6yzlYEyKg7wdZnjm3mQs2"+
                                "7kZvG3e0CAd+0Gw0EMDjIkiXT8XZv3n+iVPRwBQdLSkwItEuIoRC/ptXWv/naws3Ivj+/AKx/cKP"+
                                "PhxL13MDLeyYxpcfABIkQGWRLHqZjjoS/+F3z//uq11RcO3qrcMN+uRhM2sGh041PvXoweuJOjs/"+
                                "ePtm+2q7v9jt9ApbZAwgRmPoqUbgNeNgX61xpFk51IynQi/28Oxi+l//9JWrKaZeVCh/dnr6+s2b"+
                                "lt2epMa2S8C6hyGA1jTTbBIRMARZ58Mq++XnPnQ8Tr2io8COjGXZ/qAorSBBcEjt3HUVpEE1I/Nb"+
                                "ry1dSP2f/sjBE3G1yn2gaJQoB3aCoBD9Ar1URUtQObPgvvLyhZev9vqVGZ+zXtrt5uiQAnBYtAwN"+
                                "6oE5ccw8d6yRu0ZWsGMY5eVYKzQKjCJDrMQiLxE7KDhu1v7dZx7573/y9nmQPPCM8sqYKK+T/q64"+
                                "2IqAcaGvoxoBZ6ZmtNbaFkGRHMTBF59/5PFqHti2lnzl+7vLZqMrTh6cOTpzbanf53BmOZj703Ot"+
                                "N87d/NFD/tPHZl2tnimfHQBQD7xLrtHJ8oVucnlp8XvvvH1mCW6qSh7uI2HKusfn4JFDs+gWUAot"+
                                "TrvcRxY3cqaAAGlsiDA4Bgelw7zysi6Kj++f6z576EvfvCLOy8gPw7DRaC63Fu98JdiNH4EAzXqj"+
                                "Xm2wSCVrH0pav/S5E585Zmr5vBaLcrvogtzmyRa9XMVvd/R/e+H8Gx3oxXMWlbGJtoliS37Y1dUc"+
                                "NQCEnO8zGRX9fuoyQad81lFGvpGi1r/11BT82586eaKSB65P5bDY3phYqYQZTkrQOZmWnv2dH8x/"+
                                "+dVby9HcwEQAMD8/P8gGfGfr8W4W4dAPphvT5JwvtpYu/NzT088/1qwWS54McPudmUCAiChgLXkt"+
                                "DB595FiRZQs3F9nlpLzCRKlXH6jIwXDYOlCdQrUlSEzdmVBIKZeFeWeWO5//0Ow//fRjx4N+bFsI"+
                                "tgymTugJA659eyyqCIgIrMUiyOEDB6/enL/WzgodIKLxg0Ey4NsOuC2xYwIU0r7ZWUXa47ySLH3i"+
                                "gPcLzxyddvOeZFQOrlHpx+gbtBqFXxn762QxJoKVagkSV1XF00dnntxfidKlYjmBrA9FURovgFQG"+
                                "/sgVnrCXD6K8M130jun888f1L37q2GdPVuag5XOfYFTNUD6FSIYpdRzm7IbZB1zpCQpu7COBxJAd"+
                                "2P/Qy+dvdp0S1KK0UjS4sx3IO1NBI+VTQ5ZK3noEuv/hZ55+MlqouaXJ9v5YycYk5bM2UT7OH6OX"+
                                "U9yl6uWB9+bN7tkb7cutQSspeoV1VkhBaHTFU7OV8MBUdOqh2olp/6DJIu54kiCvTaCTlFVEACtr"+
                                "wNjgLbXThpT9MEQ4nNnU8Wf/+KL+H187Mx/O9FREBPOLC71ksGs1tDMz1Pf8SlxDFs0cZd1//OxD"+
                                "T1X6YZEAjy9lmwZ8CGAk6I11CcNyK4DVoTgEgvW4X5P80cCcPOHnJ5qZnSocOIYydYjAniKtyFNO"+
                                "SU6uY1xhpNgk3MQIq42PvVv2ZLwSdDhQSqrG22Gv6H364dlzx7wvX+xgHAKoWrXRTxIZLRw7ZWIH"+
                                "BBBgrVYjIu2yKO18/OH6Myem4+IGyR3FY9djvbklwJmPuYcMOSCUhbcEOOIPGRiAGQoQZBGhzfTq"+
                                "JtmuXcFwOu2Wv/Bjx1+6eSbN+4UOfd+vVqudbmd3Dd5mDRh1mhCwEsSNWkMEo7xz1HX/9WcfPRIM"+
                                "fO4j8GqqFamMzgHA6CUZ+zNsdVPVN8mRLFtDQFx9X0qfGVAAeKVlBMQJe362kn7Zzrp08IQMGiIq"+
                                "tiaIIKi//c61woQ5eMaYcjWWnSfobxOoWfU5AWvVqhI2nAdF9/knph5viOd6CHs6/HcAHv15X4HC"+
                                "CHnEvZ88Mf3hJuh8QMBG61qlUn5gp77xRALGmUTgKAp83yAXfpEcCPjTj8/V3KKRYlWVD0cN3y3R"+
                                "vI8gw9l+WPjCRw9G3CNhZBeFFUVqFzpuUwIIgMaZJKSSYY9dJV/63FMP74/Bc4M91v4fHGixgW09"+
                                "daj24X1xmLWVc0brahzDHqmg9ePX8zzP8xyDdsmJAD59oulzMmGM0wd95+VWKEtOBQAYwcZU/N0n"+
                                "j0znHQ9yAIjjmJD2TAWtPhSgVqkQoAbxiv5PnGoc9jPN9+4JPO8PENjYwdP7w8emwNiEmY324zDc"+
                                "qRa6DQEIoJX2vZCcGLZNI584ORfbZSOrPo4gjG0jGp89991sEAIZeTxCPudz2PnMh+a0TUrLrxLX"+
                                "cIe+7e0FVIkirZQC66edJw9PH6hqww+u9h+HgiKwrScPN+Yi8F2mmI0xerRrapuLwW1nAIZhCACG"+
                                "i6btP/PIQ56s1/5Dc3wT3A+20Jr5vd4xYAXFTKyePjRdSZbJ5UQUjrTQNheDrQhAAN8Yz/NERNvs"+
                                "4Rgem/EM5/eBWPcEKABgfcg/cWJfrcgMWACIg3hHWug2M8D3fQIUAHLZ04fDfSrxuEAZCwNsWex3"+
                                "H2A0vwnKGPj64l1SNj0xZQ7XQLlMRLTW5R63vVFBQRAAgGYOJH/q6GzEXQV7UBJ8P0FLMefZJ/b7"+
                                "ymYIoDRGQYB3roIQUZEy2icn2uYzsTnQDA0nJHZVD5ZWwfoG7y/LZzjLJ65nRlzFdT58uOlLoQBR"+
                                "oNwCRNtTRBOFJSLlmq7AenZwdLrSCBRJDneWALr/gGA9NzgyE8dGlCsUoDFGAW7zbJzJMwDA8zwA"+
                                "0Oxi2390X8OQHZXubNHg/WD5rIEQCK31ddYAQVDyRqAONSq6GIDLtdYrxuj45zaV21bqoiSAuIiZ"+
                                "T+6rE9v7Tbh7AhEA9g0cblZ8myoRhWSM2ea3JxOAUNKoAeoeNCPUbPcgo/HBRLk/cMJ7CMga3NHZ"+
                                "auQSLSyCmxAwYXvORAKUIqWUCLIr9lWh6cEPvd91GNo55YFdwgcbcSCgxCIOCdjOeJ1IgFGeUQoA"+
                                "iIvpClWUJZCdVtreP9ho743tIUQAEjsV6aoGcK70Brbpjk1I4AFoIhBCRASZjX0fC2J3rx05eDex"+
                                "VhTErqJsI4BSU63f4jkZQwI2LtFKqdKbIOC5euhxBmKHx7z8EKug0f9cZKQWAYgTAKXU+JkvW2Bi"+
                                "VcTK9wmkFniKC7WDsrcHAONVjkIo4BNXfBBxAITbDkUMCdgo2nLfCAAgcBxoJW4UEnkQMe4ErJpD"+
                                "uCo4BDYIoSERdiAaEbfYiDiGrZPyLCIA4ntqxxt6Hzwocb6n1Ej7b3cGTKrnKr9PgCSgEUhW0qEP"+
                                "ItZq301GLQkgsK8INgtWbrGVY1sqZUUd/RATgUwydF03zoAtrEc96Z2VC6C2/v6DgtvtaQAARmBm"+
                                "GJVB7kFGbKWekpH4gY1CbA8MBACF5eGRmNsesluFo7EsPkbMcguwMfT/IGEbOyCtqNTu2FSZKNOV"+
                                "2QQAgzRzoCceYftDIAoQo0pz5rKsV6QU4G2xFQHlXWgsOMhYSD+wTgDAJrHMoZJBBpLS8imEktwB"+
                                "EAk4t92wzeYyFYCVvciCtNDr52T4rh4yeq9h1cgRAUSHNCiwkwKSLrMmzCzrPrkZJg7qFQIc0kI3"+
                                "yzFw+ADPgElrQJkvE2HSPUfLKQhQeUEE8zB6v/VUmCjTwjlmBmBBWhi4AWjGe/KygbuHcckKUqeA"+
                                "bgagFACw2DvNCTNzOQmEzHwX2hkw4GoVEI4XgI71aSx3ukUe9YOKtSsBjvxTEWFSN1tpIlDqifJm"+
                                "lO1gKwIsMyEC6fkBLOfEtN0854OC1SMrFIB3damfoFfqiTTPR2/dZgxO9gNAiqIAAIeqy3BleeCU"+
                                "GptyK1+cWCc6uWb0A4Xbb24DFMqZ3l1oD1TgQAGAc27lIBzYkoat1tU8zwXAok6Uf+5Gy8EPZ8Am"+
                                "ECRG3c7cxeVebkKHVBRFnudrPjN5Hd5iBkCe58zMpPsmemehPygEQA9r38bGxZri4dVmt6ql+SBh"+
                                "8339q7VyAmQxuNlJFgdFYTwg5Yb2y7aw1QwoisI5x0jWRBcXugt9dhjIg+yObQaHKtPxW1eWEtAO"+
                                "NSNkWbb9K+O2DMaBZHnikCzpPtP5G+1cVRwqAAYEKa9sF1m982vtPpn7ZA0Yw3BODyskynpptqiX"+
                                "IHrjWrc8SRMQs3wHp0dslRETgCTLAFmAcjIvv7vQxoob2UIrF3iNzst9QGEpuNSDtxdAdIAohc3S"+
                                "PN/+wNsqGgoASdovigLQgoneuM6XBjons8kBwA9GwmCTOS1klffq5faikCUPALIs29EBNrdR6MyQ"+
                                "FikiWjQ3Bb5/cdFS9EBH5cYgSIxetzAvnbvWM9UCFQD0Nzu9ZiszdOtAtwAkgwyEMtRdv/mtd673"+
                                "CgXibYuDCdWQ9wkEBHSGwfn53oWlLPMqQirP8zRNN/7o3ZihK0iSpCgK1jr1wgtL2ZkbnVxXthUX"+
                                "2kYS4wMMhAJNVzX/5sz1HvoFaYe0U/0D5RV0aw5DXH9iCAhIkvYFwaEegP/109cWVTNHA8hDq+AB"+
                                "2Cm2FsNRm6vgbN/71oWcvRgARKQ76O50zt9+BghAt98vfWvnxd+74t5YsIWOb5uhvE8csc1Qer+J"+
                                "iv/i9NVb4BXkEeIgG5TBmx1hrRDHp4MAyHD1KJztJwkpyJR304RfffVSAoGARiGUTXeKAXwwY0Fb"+
                                "7gpeAQtQQdHl5ewbZ2/0w2aBBgB6vd4uCqe2dV6QiHS7XWutRZUGjR9car9xrZ/qhkUNW64w9wk2"+
                                "ODoFmrZqfO31KwvOS5VviZI0zbJsF21v16AsbJEkiZCySH3t/8H3LlyTRoqeIG+zBu8DgTWzdmVF"+
                                "lLX3qSLnJn5lWb14NrVepTyJrt1r765ucLsECEC723XCjijz4lcW4BvvdjKv7tCTBylVyUgZxksc"+
                                "f/k75+ZVlJMPiP20n2Tp7jTBtk7MKlHYotfrIagcw/mw+eXvnb/UBoex3Jv3ou8Yt9/fKQgF+gMz"+
                                "8623F1+7kfT9qYK0iHS7/cn3Tt8GOzi4FQGyPI+jGLVx5OXpAPpLjx874km+el70Bxi3qz4kZFGp"+
                                "aZzuhL/x9XPXdL2vYyDq9Xrdfm+shZ1hpyfnCjsbxSELKKIb871avXpi1g/cAEEARofQ7qIjdx+T"+
                                "0l3DU30FyKG/gI0v/eU7r/VM4sVMmsXOLy2MwsHvBwFgrTVa+17AgAWaq5evPn54qhl7xI7AwfDw"+
                                "1fsIKGXhSU5hx8z+4avXvvpOtx3OWaWRYHF5OcuzlcV6F83vmAAByLI8DCKtlCNVFMWN67dOnTxZ"+
                                "05knSXnq8r0PQcKhvBhGxYSbm3OliUGq5zVfvIq/+e0rbb+Zah8Ie71ep1u6vrsPe+3m9HQRYct+"+
                                "GDEpJNXqJr1u74mjB3wpCASHFzfe20QgjggQWDkkfaXP66x+8gamebqlf+XFt65SPdUhkylsPr+4"+
                                "yHCnv3SXd8gUrlCEgR+KkDPh9flFKooTRw55nGnJ7nXpAyCMW/tj2FgASjjQtTPZzK/82Wtn8yDx"+
                                "akLGiSwsLe7J7dq7JAAB0izzPaM8v0CF2rt6eTEw9PBD06acBx/oPWUjUhz5qapdsvVffeH0ay01"+
                                "CKZy5QvS8vJyP+3Dzk8J3YidEzC2zCZJ6oe+UYYFMzLnL9+qeubIvlkjmcZcAFc17T0EGhk8uIni"+
                                "HjOFBClV1Xd59tdePP3SAvejmYIMEHW6rXavs1c/a/fXWCEAg2RpGoY+aS9XngW8+O68F+DhfdPE"+
                                "TMgo9+DOmvEeTRRjQf5A1y/Z2q+9ePo7N2wvnE2UD0T9fn+ptbyx6GHXU2FbBGxxShALp2kWhoEi"+
                                "YqQB6bcuLyCqwwf3B5xrKO69KSAAIiiAE51XQerrqdNZ81dfPP3SPPfC2RyNEA0Gg8XWouxpBny7"+
                                "BKz7yziYOcvzIAhAm1wFjsy7F28NksHDhw8ZhQSs5N5LTk7wVwR0TmHPm35jmb70Z6+/3lK9cCZV"+
                                "viCmWXZr8dbGiNvdsYLWwTmX5XkYBIo0I1oTvHOjffnW8oGDD0/54LkUAUR2GzfdeMnOGGTF8R59"+
                                "ZI03PuG7m0xoZCDtwOuZmReuwpdefOt8FvWDhkUDBEkyWFicX5X+3inWPbtR2zmXZpnv+aS9DH2n"+
                                "/ZvL/XMXrsxOTzWmpkRGtyDvomncMkozHvnA1f+OCNjG/lIARrIQDnR9Qaa+8srV3/z2levYSLxa"+
                                "rjxU2O/3F5YXx++J2EPs3ZXmAI5dkgyMZzxtQNBqv53jD965OQD/4NxMpADBErhRjH29CzMxjrSl"+
                                "ENdLHwCBEFBQAEs9j5vNg7G7nQByCgZm5o1O8KW/Ov/H7/RawVSmI0skRO1ue6G1/N7p0L0kAABY"+
                                "JBkMNGkTBExeobyEonOXb1y6cqM5MxtXGwiEILRptnhSHGl7o3hDQwKbzYNRvTgIoqDKKExWaaQM"+
                                "AAAERUlEQVTM1Dw2X3jz1m98/fzrfa8d7cuV55QuHLdareVee7Xd98DB3GMCAEAA0jSxzvqeh0ox"+
                                "oDXh1QF/59zNVoZT0w/VfWVcDiiIbv0IFgHCDfNgy/qWUrY0PsZlNJ3WtyArtZQADFTouK2a35n3"+
                                "/udfnv3q2+0bpp76FYckhFmRLbWXe0l/1LP3Cu9h24EXNusN3/dLsy10SW2wcMzwT//IzI+fnJ0K"+
                                "nbF93xUKCpLVZMjae7u2gRUCALZRqEEOtEVTqGBA0cVW9uevXvzGeTuvKv2gmpEHAIjY63eWWi1X"+
                                "VvjsPtC5LeycgIkdKqOGAjJ0U0rtW6/Xq1FVK0VceFx4ReIV/Yer+PxHjn3sSPWwN4hdx7gUMd9a"+
                                "fBuJuT1VK2cqIYsIgrLoFbrSwdqbbfr6m9f++p35JfYKr1KQb1E7Amttq9PpD/obW92Ty2s36eMu"+
                                "vzGJAFy9MW+lad/zG9VGea67iCjmoOjXs6VjETz7SPVjJ/YdagQBpMrlinMtVonDDQnC3RBAikUx"+
                                "UQHGkmcp6BR09mbvm2euffeKvSmmF0xlJhxGpcUmSbLcbjt28N7IelPsVgVNomHC6wopDMN6teob"+
                                "TwSNOONy7RJjkzq5J/bXP37yocfnooOhq0Hfs13iHKQAwlXLVcYJnrC/fGzvJgowehbDVFeWOL7Q"+
                                "ta9ean/3/PUL7WwAvjORVX6GJEozSJZly+12lmfvv7v4PhFQggCrcVyt1D1joDRIhDzOo7RVsf39"+
                                "Pjw6h08dnj7+UL0ZmdgjQ5bYIjA5h8AEgsKAPLxldmjIEmBZHIlMipEYFJOxQsv94mY7PXNl6bVr"+
                                "/fMtWGbdM9XMrxSkQRwRCXKapp1OJ8myu3UY1a4X4Q2XYU56wIaruAmpEkVRFAVBgELIzrBV4rTN"+
                                "gFMjrkIyF3sHGuGJudrBZjxT9WPFVU8FijWwJiYRFIeoSj1lReWgBoV0LXUyudlOLy92311sXV0e"+
                                "LKUuA+3IEx045edAQoqRnNgsy9rdbp7n5Q14d/Ib7wR7PQN28sjID+Ko6vu+1npEEomIEWds5hVJ"+
                                "5Po+c6Sg6sFUCLEHgQHfI59IaRIG5yS1Lsk5tdDLoJ1CK4MUIFP+QPu5CVj5KycslNZnXqRpmg4G"+
                                "g7S47T6We5mAO3/wyERRpIIgiMPQ87ySCQIkdkpYs0NgJQ7YAjsEEHG0lnZXFrCiQlQCBEo51IxU"+
                                "IDKSkBIgQM6KIsuyJEmyrGBhgEmj/v3G3SFg/MzNFSa00p7nRYFnjK+1VrTm+GUWgdE5GEIoIgoQ"+
                                "Rms0CQAwjTxVAQDkoiiclSxPkiwbUzXvx7jePnZJwJ54J5ta1uUiQURaa8/zjFLlrSxEBELrzuUv"+
                                "WyhPRyqP6GGx1tqsGKLcLiHrHrdZKuxu4f0mYEfuTPmU8vJxIkJEQk1EUFYEl+fEMK9yAG4lV7Xp"+
                                "MxBHF1vIhtfvUo33/wdJIyS2pV5B3AAAAABJRU5ErkJggg=="

                    //Finally, assert that we got the image we were expecting back (by asserting it equals the base64 we expect)
                    assertEquals(base64,encoded)
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    }
