package com.zzwl.bakeMedicine.ui.about

import android.os.Bundle
import android.text.Html
import com.alibaba.android.arouter.facade.annotation.Route
import com.g.base.ui.BaseActivity
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.databinding.ViewScrollTextBinding
import com.zzwl.bakeMedicine.router.RouterPage


/**
 * Created by G on 2017/9/2 0002.
 */
@Route(path = RouterPage.OauthAgreementActivity)
class UserAgreementActivity : BaseActivity<ViewScrollTextBinding>() {
    override var hasToolbar: Boolean = true

    private val soft = "<b>本软件</b>"
    val agreement =
            """
    <h1 style="text-align: center">用户服务协议</h1>
    <h2>一、特别提示 </h2>
    <p>
        在此特别提醒您（用户）在注册成为 $soft 用户之前，请认真阅读本《 $soft 用户服务协议》（以下简称“协议”），确保您充分理解本协议中各条款。请您审慎阅读并选择接受或不接受本协议。除非您接受本协议所有条款，否则您无权注册、登录或使用本协议所涉服务。您的注册、登录、使用等行为将视为对本协议的接受，并同意接受本协议各项条款的约束。
        本协议约定 $soft 与用户之间关于“ $soft ”服务（以下简称“服务”）的权利义务。“用户”是指注册、登录、使用本服务的个人。本协议可由 $soft 随时更新，更新后的协议条款一旦公布即代替原来的协议条款，恕不再另行通知，用户可在本APP中查阅最新版协议条款。在修改协议条款后，如果用户不接受修改后的条款，请立即停止使用 $soft 提供的服务，用户继续使用 $soft 提供的服务将被视为接受修改后的协议。
    </p>
    <h2>二、账号注册</h2>
    <p>
        1、用户在使用本服务前需要注册一个“ $soft ”账号。“ $soft ”账号应当使用手机号码绑定注册，请用户使用尚未与“ $soft ”账号绑定的手机号码，以及未被 $soft 根据本协议封禁的手机号码注册“ $soft ”账号。 $soft 可以根据用户需求或产品需要对账号注册和绑定的方式进行变更，而无须事先通知用户。
        <br>2、如果注册申请者有被 $soft 封禁的先例或涉嫌虚假注册及滥用他人名义注册，及其他不能得到许可的理由，  $soft 将拒绝其注册申请。
        <br>3、鉴于“ $soft ”账号的绑定注册方式，您同意 $soft 在注册时将允许您的手机号码及手机设备识别码等信息用于注册。
        <br>4、在用户注册及使用本服务时， $soft 需要搜集能识别用户身份的个人信息以便 $soft 可以在必要时联系用户，或为用户提供更好的使用体验。 $soft 搜集的信息包括但不限于用户的姓名、地址； $soft 同意对这些信息的使用将受限于第三条用户个人隐私信息保护的约束。
    </p>
    <h2>三、账户安全</h2>
    <p>
        1、用户一旦注册成功，成为 $soft 的用户，将得到一个用户名和密码，并有权利使用自己的用户名及密码随时登陆 $soft 。
        <br>2、用户对用户名和密码的安全负全部责任，同时对以其用户名进行的所有活动和事件负全责。
        <br>3、用户不得以任何形式擅自转让或授权他人使用自己的 $soft 用户名。
        <br>4、如果用户泄漏了密码，有可能导致不利的法律后果，因此不管任何原因导致用户的密码安全受到威胁，应该立即和 $soft 客服人员取得联系，否则后果自负。
    </p>
    <h2>四、用户声明与保证</h2>
    <p style="word-break: break-all ;display: block">
        1、用户承诺其为具有完全民事行为能力的民事主体，且具有达成交易履行其义务的能力。
        <br>2、用户有义务在注册时提供自己的真实资料，并保证诸如手机号码、姓名、所在地区等内容的有效性及安全性，保证 $soft 工作人员可以通过上述联系方式与用户取得联系。同时，用户也有义务在相关资料实际变更时及时更新有关注册资料。\n
        <br>3、用户通过使用 $soft 的过程中所制作、上载、复制、发布、传播的任何内容，包括但不限于账号头像、名称、用户说明等注册信息及认证资料，或文字、语音、图片、视频、图文等发送、回复和相关链接页面，以及其他使用账号或本服务所产生的内容，不得违反国家相关法律制度，包含但不限于如下原则：
        <br>&nbsp;&nbsp;&nbsp;&nbsp;（1）反对宪法所确定的基本原则的；
        <br>&nbsp;&nbsp;&nbsp;&nbsp;（2）危害国家安全，泄露国家秘密，颠覆国家政权，破坏国家统一的；
        <br>&nbsp;&nbsp;&nbsp;&nbsp;（3）损害国家荣誉和利益的；
        <br>&nbsp;&nbsp;&nbsp;&nbsp;（4）煽动民族仇恨、民族歧视，破坏民族团结的；
        <br>&nbsp;&nbsp;&nbsp;&nbsp;（5）破坏国家宗教政策，宣扬邪教和封建迷信的；
        <br>&nbsp;&nbsp;&nbsp;&nbsp;（6）散布谣言，扰乱社会秩序，破坏社会稳定的；
        <br>&nbsp;&nbsp;&nbsp;&nbsp;（7）散布淫秽、色情、赌博、暴力、凶杀、恐怖或者教唆犯罪的；
        <br>&nbsp;&nbsp;&nbsp;&nbsp;（8）侮辱或者诽谤他人，侵害他人合法权益的；
        <br>&nbsp;&nbsp;&nbsp;&nbsp;（9）含有法律、行政法规禁止的其他内容的。
        <br>4、用户不得利用“ $soft ”账号或本服务制作、上载、复制、发布、传播下干扰“ $soft ”正常运营，以及侵犯其他用户或第三方合法权益的内容：
        <br>&nbsp;&nbsp;&nbsp;&nbsp;（1）含有任何性或性暗示的；
        <br>&nbsp;&nbsp;&nbsp;&nbsp;（2）含有辱骂、恐吓、威胁内容的；
        <br>&nbsp;&nbsp;&nbsp;&nbsp;（3）含有骚扰、垃圾广告、恶意信息、诱骗信息的；
        <br>&nbsp;&nbsp;&nbsp;&nbsp;（4）涉及他人隐私、个人信息或资料的；
        <br>&nbsp;&nbsp;&nbsp;&nbsp;（5）侵害他人名誉权、肖像权、知识产权、商业秘密等合法权利的；
        <br>&nbsp;&nbsp;&nbsp;&nbsp;（6）含有其他干扰本服务正常运营和侵犯其他用户或第三方合法权益内容的信息。
    </p>
    <h2>六、服务的终止</h2>
    <p>
        1、在下列情况下， $soft 有权终止向用户提供服务：
        <br>&nbsp;&nbsp;&nbsp;&nbsp;(1）在用户违反本服务协议相关规定时， $soft 有权终止向该用户提供服务。如该用户再一次直接或间接或以他人名义注册为用户的，一经发现， $soft 有权直接单方面终止向该用户提供服务；
        <br>&nbsp;&nbsp;&nbsp;&nbsp;(2）如 $soft 通过用户提供的信息与用户联系时，发现用户在注册时填写的联系方式已不存在或无法接通， $soft 以其它联系方式通知用户更改，而用户在三个工作日内仍未能提供新的联系方式， $soft 有权终止向该用户提供服务；
        <br>&nbsp;&nbsp;&nbsp;&nbsp;(3）用户不得通过程序或人工方式进行刷量或作弊，若发现用户有作弊行为， $soft 将立即终止服务，并有权扣留账户内金额；
        <br>&nbsp;&nbsp;&nbsp;&nbsp;(4）一旦 $soft 发现用户提供的数据或信息中含有虚假内容， $soft 有权随时终止向该用户提供服务；
        <br>&nbsp;&nbsp;&nbsp;&nbsp;(5）本服务条款终止或更新时，用户明示不愿接受新的服务条款；
        <br>&nbsp;&nbsp;&nbsp;&nbsp;(6）其它 $soft 认为需终止服务的情况。
    </p>
            """.trimIndent()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_scroll_text)
        toolbar.title = "用户服务协议"
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_toolbar_24dp)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        contentView.text.text = Html.fromHtml(agreement)
        showContentView()
    }
}


