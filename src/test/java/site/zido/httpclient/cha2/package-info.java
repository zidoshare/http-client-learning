/**
 * 连接管理
 *
 * <h1>http 连接路由 </h1>
 * <p>
 * HttpClient 能够直接或通过可能涉及多个中间连接的路由建立到目标主机的连接 - 也称为跳.
 * HttpClient 将路由的连接区分为普通,隧道和分层.
 * 使用多个中间代理来隧道连接到目标主机称为代理链.
 * 通过连接到目标或第一个也是唯一的代理来建立普通路由.
 * 隧道路由是通过连接到第一个隧道并通过代理链到目标的隧道来建立的.
 * 没有代理的路由不能被隧道化.
 * 通过在现有连接上分层协议来建立分层路由.
 * 协议只能通过隧道分层到目标,或通过没有代理的直接连接.
 *
 * <h1> 路由计算 </h1>
 * <p>
 * RouteInfo  接口表示有关到目标主机的确定路由的信息,涉及一个或多个中间步骤或跳.
 * HttpRoute  是  RouteInfo  的具体实现,无法更改（不可变）.
 * HttpTracker  是一个可变的  RouteInfo  实现,由  HttpClient  内部使用,用于跟踪剩余的跳转到最终路由目标.
 * 在成功执行到路线目标的下一跳之后,可以更新  HttpTracker  .
 * HttpRouteDirector 是一个辅助类,可用于计算路由中的下一步.
 * 该类由 HttpClient 内部使用.
 * HttpRoutePlanner 是一个接口,表示根据执行上下文计算到给定目标的完整路由的策略.
 * HttpClient  附带两个默认的  HttpRoutePlanner  实现.
 * SystemDefaultRoutePlanner 基于 java . net . ProxySelector .
 * 默认情况下,它将从系统属性或运行应用程序的浏览器中获取 JVM 的代理设置.
 * DefaultProxyRoutePlanner 实现不使用任何 Java 系统属性,也不使用任何系统或浏览器代理设置.
 * 它始终通过相同的默认代理计算路由.
 */
package site.zido.httpclient.cha2;