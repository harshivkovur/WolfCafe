import React, { useState } from 'react'
import { registerAPICall } from '../services/AuthService'
import { useNavigate } from 'react-router-dom';


const RegisterComponent = () => {

    const [name, setName] = useState('')
    const [username, setUsername] = useState('')
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const navigate = useNavigate();

    function handleRegistrationForm(e) {
        e.preventDefault();
        if (!validateForm()) return;

        const register = {name, username, email, password}

        console.log(register)

        registerAPICall(register).then((response) => {
            console.log(response.data)
            navigate('/login')
        }).catch(error => {
            console.error(error)
            alert(error.response.data.message);
        });
    }

      // --- Form validation ---
  const validateForm = () => {
    if (!name) {
      alert('Name Field is Blank');
      return false;
    }
    if (!username) {
      alert('Username Field is Blank');
      return false;
    }
    if (!email) {
      alert('Email Field is Blank');
      return false;
    }
    else {
      const emailRegex = /^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$/;
      if (!emailRegex.test(email)) {
        alert('Email is Invalid');
        return false;
      }
    }

    if (!password) {
      alert('Password Field is Blank');
      return false;
    }

    return true;
  };

    return (
        <div className="container">
            <div className="row">
                <div className="col-md-6 offset-md-3 offset-md-3">
                    <div className="card">
                        <div className="card-header">
                            <h2 className="text-center">User Registration Form</h2>
                        </div>
                        <div className="card-body">
                            <form>
                            <div className='row mb-3'>
                                <label className='col-md-3 control-label'>Name</label>
                                <div className='col-md-9'>
                                    <input
                                        type='text'
                                        name='name'
                                        className='form-control'
                                        placeholder='Enter name'
                                        value={name}
                                        onChange={(e) => setName(e.target.value)}
                                    >
                                    </input>
                                </div>
                            </div>

                            <div className='row mb-3'>
                                <label className='col-md-3 control-label'>Username</label>
                                <div className='col-md-9'>
                                    <input
                                        type='text'
                                        name='username'
                                        className='form-control'
                                        placeholder='Enter username'
                                        value={username}
                                        onChange={(e) => setUsername(e.target.value)}
                                    >
                                    </input>
                                </div>
                            </div>

                            <div className='row mb-3'>
                                <label className='col-md-3 control-label'>Email</label>
                                <div className='col-md-9'>
                                    <input
                                        type='text'
                                        name='email'
                                        className='form-control'
                                        placeholder='Enter email'
                                        value={email}
                                        onChange={(e) => setEmail(e.target.value)}
                                    >
                                    </input>
                                </div>
                            </div>

                            <div className='row mb-3'>
                                <label className='col-md-3 control-label'>Password</label>
                                <div className='col-md-9'>
                                    <input
                                        type='password'
                                        name='password'
                                        className='form-control'
                                        placeholder='Enter password'
                                        value={password}
                                        onChange={(e) => setPassword(e.target.value)}
                                    >
                                    </input>
                                </div>
                            </div>

                            <div className='form-group mb-3'>
                                <button className='btn btn-primary' onClick={(e) => handleRegistrationForm(e)}>Submit</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
  )
}

export default RegisterComponent